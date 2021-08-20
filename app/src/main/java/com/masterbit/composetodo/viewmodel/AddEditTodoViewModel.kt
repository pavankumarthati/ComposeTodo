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
class AddEditTodoViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle, private val tasksRepository: TaskRepository): ViewModel() {

    var task = mutableStateOf(Task())
    var initialLoad = mutableStateOf(true)

    suspend fun loadTask(taskId: Long) {
        tasksRepository.getTask(taskId)?.let {
            task.value = it
        }
    }

    fun saveTask() {
        viewModelScope.launch {
            tasksRepository.insertOrUpdateTask(task.value)
        }
    }
}