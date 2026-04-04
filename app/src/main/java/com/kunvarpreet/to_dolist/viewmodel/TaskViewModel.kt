package com.kunvarpreet.to_dolist.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kunvarpreet.to_dolist.data.Task
import com.kunvarpreet.to_dolist.data.TaskDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = TaskDatabase
        .getDatabase(application)
        .taskDao()
    val tasks: StateFlow<List<Task>> =
        dao.getAllTasks()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                emptyList()
            )
    fun addTask(title: String, date: Long?, time: Long?) {
        viewModelScope.launch {
            val combinedMillis = time ?: date
            dao.insertTask(
                Task(
                    title = title,
                    dateMillis = combinedMillis,
                    timeMillis = time
                )
            )
        }
    }
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task)
        }
    }
    fun toggleTask(task: Task, checked: Boolean) {
        viewModelScope.launch {
            dao.updateTask(task.copy(isDone = checked))
        }
    }
    fun clearAllTasks() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }
}