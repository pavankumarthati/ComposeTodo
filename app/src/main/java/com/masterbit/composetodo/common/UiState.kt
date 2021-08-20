package com.masterbit.composetodo.common

import androidx.compose.ui.platform.LocalContext

data class UiState<T>(
    val loading: Boolean,
    val data: T? = null,
    val error: Int? = null
) {

    val hasError: Boolean
        get() = error != null

    val initialLoad: Boolean
        get() = loading && (data == null) && (error == null)
}

fun <T> UiState<T>.copyWithResult(result: Result<T>): UiState<T> {
    return when (result) {
        is Result.Success -> {
            copy(loading = false, data = result.data, error = null)
        }
        is Result.Error -> {
            copy(loading = false, data = null, error = result.error)
        }
    }
}