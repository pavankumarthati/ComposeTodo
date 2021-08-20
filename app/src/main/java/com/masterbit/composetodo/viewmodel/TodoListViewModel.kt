package com.masterbit.composetodo.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masterbit.composetodo.R
import com.masterbit.composetodo.common.Result
import com.masterbit.composetodo.common.TaskType
import com.masterbit.composetodo.data.TaskRepository
import com.masterbit.composetodo.db.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle, private val tasksRepository: TaskRepository): ViewModel() {

    private val _filterTypeState = mutableStateOf<TaskType>(TaskType.ALL_TASKS)
    val filterTypeState: State<Int>
    get() = derivedStateOf {
        when (_filterTypeState.value) {
            TaskType.ACTIVE_TASKS -> {
                R.string.active_tasks
            }
            TaskType.ALL_TASKS -> {
                R.string.all_tasks
            }
            TaskType.COMPLETED_TASKS -> {
                R.string.completed_tasks
            }
    } }

    fun refresh(): Flow<Result<List<Task>>> {
        return when (_filterTypeState.value) {
            TaskType.ALL_TASKS -> {
                tasksRepository.getAllTasks()
            }
            TaskType.COMPLETED_TASKS -> {
                tasksRepository.getCompletedTasks()
            }
            TaskType.ACTIVE_TASKS -> {
                tasksRepository.getActiveTasks()
            }
        }.map {
            Result.Success( if (it.isNullOrEmpty()) emptyList() else it) as com.masterbit.composetodo.common.Result<List<Task>>
        }.catch { e ->
            emit(Result.Error(R.string.no_tasks_found))
        }.flowOn(Dispatchers.IO)
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.insertOrUpdateTask(task)
        }
    }

    fun deleteCompletedTasks() {
        viewModelScope.launch {
            tasksRepository.deleteCompletedTasks()
        }
    }

    fun setFilterType(taskType: TaskType) {
        if (taskType != _filterTypeState.value) {
            _filterTypeState.value = taskType
        }
    }

}