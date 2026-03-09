package com.kunvarpreet.to_dolist.data
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val date: String = "",
    val time: String = "",
    var isDone: Boolean = false
)