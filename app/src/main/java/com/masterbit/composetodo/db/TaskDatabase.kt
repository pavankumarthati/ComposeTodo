package com.masterbit.composetodo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.masterbit.composetodo.db.Task
import com.masterbit.composetodo.db.TaskDao

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        private var INSTANCE: TaskDatabase? = null
        fun getInstance(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context)
            }
        }

        private fun buildDatabase(context: Context): TaskDatabase {
            return Room.databaseBuilder(context, TaskDatabase::class.java, "tasks.db").build()
        }
    }


}