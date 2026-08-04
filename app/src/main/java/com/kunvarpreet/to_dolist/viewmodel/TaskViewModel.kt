package com.kunvarpreet.to_dolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kunvarpreet.to_dolist.data.RepeatInterval
import com.kunvarpreet.to_dolist.data.Task
import com.kunvarpreet.to_dolist.data.TaskDatabase
import com.kunvarpreet.to_dolist.notifications.TaskAlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

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

    fun addTask(
        title: String,
        date: Long?,
        time: Long?,
        hasReminder: Boolean = false,
        repeatInterval: RepeatInterval = RepeatInterval.NONE
    ) {
        viewModelScope.launch {
            val combinedMillis = time ?: date
            val taskToInsert = Task(
                title = title,
                dateMillis = combinedMillis,
                timeMillis = time,
                hasReminder = hasReminder,
                repeatInterval = repeatInterval
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
                if (task.repeatInterval != RepeatInterval.NONE) {
                    val (nextDateMillis, nextTimeMillis) = calculateNextOccurrence(
                        task.dateMillis,
                        task.timeMillis,
                        task.repeatInterval
                    )
                    val nextTask = Task(
                        title = task.title,
                        dateMillis = nextDateMillis,
                        timeMillis = nextTimeMillis,
                        isDone = false,
                        hasReminder = task.hasReminder,
                        repeatInterval = task.repeatInterval
                    )
                    val generatedId = dao.insertTask(nextTask)
                    val savedNextTask = nextTask.copy(id = generatedId.toInt())
                    if (savedNextTask.hasReminder) {
                        alarmScheduler.schedule(savedNextTask)
                    }
                }
            } else if (updatedTask.hasReminder) {
                alarmScheduler.schedule(updatedTask)
            }
        }
    }

    private fun calculateNextOccurrence(
        dateMillis: Long?,
        timeMillis: Long?,
        interval: RepeatInterval
    ): Pair<Long?, Long?> {
        val cal = Calendar.getInstance()
        val baseMillis = dateMillis ?: cal.timeInMillis
        cal.timeInMillis = baseMillis

        when (interval) {
            RepeatInterval.DAILY -> cal.add(Calendar.DAY_OF_YEAR, 1)
            RepeatInterval.WEEKLY -> cal.add(Calendar.WEEK_OF_YEAR, 1)
            RepeatInterval.MONTHLY -> cal.add(Calendar.MONTH, 1)
            RepeatInterval.NONE -> {}
        }
        val nextDate = cal.timeInMillis

        val nextTime = if (timeMillis != null) {
            val timeCal = Calendar.getInstance()
            timeCal.timeInMillis = timeMillis
            when (interval) {
                RepeatInterval.DAILY -> timeCal.add(Calendar.DAY_OF_YEAR, 1)
                RepeatInterval.WEEKLY -> timeCal.add(Calendar.WEEK_OF_YEAR, 1)
                RepeatInterval.MONTHLY -> timeCal.add(Calendar.MONTH, 1)
                RepeatInterval.NONE -> {}
            }
            timeCal.timeInMillis
        } else null

        return Pair(nextDate, nextTime)
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
        hasReminder: Boolean = task.hasReminder,
        repeatInterval: RepeatInterval = task.repeatInterval
    ) {
        viewModelScope.launch {
            val combinedMillis = time ?: date
            val updatedTask = task.copy(
                title = title,
                dateMillis = combinedMillis,
                timeMillis = time,
                hasReminder = hasReminder,
                repeatInterval = repeatInterval
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