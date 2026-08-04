package com.kunvarpreet.to_dolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kunvarpreet.to_dolist.data.Task
import com.kunvarpreet.to_dolist.data.TaskDatabase
import com.kunvarpreet.to_dolist.notifications.TaskAlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = TaskDatabase
        .getDatabase(application)
        .taskDao()

    private val alarmScheduler = TaskAlarmScheduler(application)

    val tasks: StateFlow<List<Task>> =
        dao.getAllTasks()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                emptyList()
            )

    fun addTask(title: String, date: Long?, time: Long?, hasReminder: Boolean = false) {
        viewModelScope.launch {
            val combinedMillis = time ?: date
            val taskToInsert = Task(
                title = title,
                dateMillis = combinedMillis,
                timeMillis = time,
                hasReminder = hasReminder
            )
            val generatedId = dao.insertTask(taskToInsert)
            val savedTask = taskToInsert.copy(id = generatedId.toInt())
            if (savedTask.hasReminder) {
                alarmScheduler.schedule(savedTask)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            alarmScheduler.cancel(task)
            dao.deleteTask(task)
        }
    }

    fun toggleTask(task: Task, checked: Boolean) {
        viewModelScope.launch {
            val updatedTask = task.copy(isDone = checked)
            dao.updateTask(updatedTask)
            if (checked) {
                alarmScheduler.cancel(updatedTask)
            } else if (updatedTask.hasReminder) {
                alarmScheduler.schedule(updatedTask)
            }
        }
    }

    fun toggleReminder(task: Task, hasReminder: Boolean) {
        viewModelScope.launch {
            val updatedTask = task.copy(hasReminder = hasReminder)
            dao.updateTask(updatedTask)
            if (hasReminder && !updatedTask.isDone) {
                alarmScheduler.schedule(updatedTask)
            } else {
                alarmScheduler.cancel(updatedTask)
            }
        }
    }

    fun updateTask(
        task: Task,
        title: String,
        date: Long?,
        time: Long?,
        hasReminder: Boolean = task.hasReminder
    ) {
        viewModelScope.launch {
            val combinedMillis = time ?: date
            val updatedTask = task.copy(
                title = title,
                dateMillis = combinedMillis,
                timeMillis = time,
                hasReminder = hasReminder
            )
            dao.updateTask(updatedTask)
            if (updatedTask.hasReminder && !updatedTask.isDone) {
                alarmScheduler.schedule(updatedTask)
            } else {
                alarmScheduler.cancel(updatedTask)
            }
        }
    }

    fun clearAllTasks() {
        viewModelScope.launch {
            tasks.value.forEach { task ->
                alarmScheduler.cancel(task)
            }
            dao.clearAll()
        }
    }
}