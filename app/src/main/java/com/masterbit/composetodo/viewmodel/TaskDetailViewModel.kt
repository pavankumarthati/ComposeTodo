package com.masterbit.composetodo.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masterbit.composetodo.data.TaskRepository
import com.masterbit.composetodo.db.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle, private val taskRepository: TaskRepository): ViewModel() {

    private val _task = mutableStateOf<Task?>(null)
    val task: State<Task?> = _task

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            val task = taskRepository.getTask(taskId)
            _task.value = task
        }
    }

    fun deleteTask() {
        _task.value?.let {
            viewModelScope.launch {
                taskRepository.delete(it)
                _task.value = null
            }
        }
    }

    fun completeTask(completed: Boolean) {
        _task.value?.let {
            viewModelScope.launch {
                taskRepository.insertOrUpdateTask(it.copy(completed = completed))
                loadTask(it.id!!)
            }
        }
    }

}