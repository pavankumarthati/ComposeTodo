package com.masterbit.composetodo.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val INVALID_ROW_ID = -1L
@Entity(tableName = "tasks")
data class Task(
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "completed") val completed: Boolean = false,
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long? = null
)