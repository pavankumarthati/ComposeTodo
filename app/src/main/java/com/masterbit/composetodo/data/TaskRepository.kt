package com.masterbit.composetodo.data

import com.masterbit.composetodo.db.Task
import com.masterbit.composetodo.db.TaskDatabase
import com.masterbit.composetodo.di.IODispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskDatabase: TaskDatabase, @IODispatcher private val ioDispatcher: CoroutineDispatcher) {

    val tasks = listOf(
        Task("Get Laundry", "", false, 1),
        Task("Get Laundry", "", false, 2),
        Task("Get Laundry", "", false, 3),
    )

    fun getCompletedTasks(): Flow<List<Task>> {
        return taskDatabase.taskDao().queryCompletedTasks()
    }

    fun getActiveTasks(): Flow<List<Task>> {
        return taskDatabase.taskDao().queryActiveTasks()
    }

    fun getAllTasks(): Flow<List<Task>> {
        return taskDatabase.taskDao().queryAllTasks()
    }

    suspend fun getTask(taskId: Long): Task? {
        return withContext(ioDispatcher) {
            taskDatabase.taskDao().getTask(taskId)
        }
    }

    fun getTaskFlow(taskId: Long): Flow<Task> {
        return taskDatabase.taskDao().getTaskFlow(taskId)
    }

    suspend fun deleteCompletedTasks() {
        withContext(ioDispatcher){
            taskDatabase.taskDao().deleteCompletedTasks()
        }
    }

    suspend fun delete(task: Task) {
        withContext(ioDispatcher) {
            taskDatabase.taskDao().delete(task)
        }
    }

    suspend fun insertOrUpdateTask(task: Task) {
        withContext(ioDispatcher) {
            taskDatabase.taskDao().insert(task)
        }
    }
}