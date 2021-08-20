package com.masterbit.composetodo.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import com.masterbit.composetodo.db.Task
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ProducerResult<T>(
    val result: State<T>,
    val refresh: () -> Unit
)

@Composable
fun <Producer, T> produceUiStateWithFlow(
    producer: Producer,
    block: Producer.() -> Flow<Result<T>>
): ProducerResult<UiState<T>> {
    val refreshChannel = remember { Channel<Unit>(Channel.CONFLATED) }

    val result = produceState(initialValue = UiState<T>(loading = true)) {
        value = UiState(loading = true)
        refreshChannel.send(Unit)

        var job: Job? = null
        for (refreshEvent in refreshChannel) {
            job?.cancel()
            value = UiState(loading = true)
            job = launch {
                producer.block().collectLatest {
                    value = value.copyWithResult(it)
                }
            }
        }
    }

    return ProducerResult(
        result = result,
        refresh = {
            refreshChannel.trySend(Unit)
        }
    )
}

@Composable
fun <Producer, T> produceUiState(
    producer: Producer,
    block: Producer.() -> Result<T>
): ProducerResult<UiState<T>> {
    val refreshChannel = remember { Channel<Unit>(Channel.CONFLATED) }

    val result = produceState(initialValue = UiState<T>(loading = true)) {

        value = UiState(loading = true)
        refreshChannel.send(Unit)

        for (refreshEvent in refreshChannel) {
            value = value.copy(loading = true)
            value = value.copyWithResult(producer.block())
        }
    }

    return ProducerResult(
        result = result,
        refresh = { refreshChannel.trySend(Unit) }
    )
}