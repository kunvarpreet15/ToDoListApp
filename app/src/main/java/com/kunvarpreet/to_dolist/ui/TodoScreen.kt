package com.kunvarpreet.to_dolist.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kunvarpreet.to_dolist.viewmodel.TaskViewModel
import java.util.Calendar
import java.util.Locale

@Composable
fun TodoScreen(
    viewModel: TaskViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var taskText by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            selectedDate = "$day/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding()
    ) {
        Row {
            TextField(
                value = taskText,
                onValueChange = { taskText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter task") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (taskText.isNotBlank()) {
                    viewModel.addTask(taskText, selectedDate, selectedTime)
                    taskText = ""
                }
            }) {
                Text("Add")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Button(onClick = { datePicker.show() }) {
                Text(selectedDate.ifEmpty { "Pick Date" })
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { timePicker.show() }) {
                Text(selectedTime.ifEmpty { "Pick Time" })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                TaskItem(
                    task = task,
                    onDelete = { viewModel.deleteTask(task) },
                    onCheckedChange = {
                        viewModel.toggleTask(task, it)
                    }
                )
            }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .navigationBarsPadding()
        ) {
            Button(
                onClick = { viewModel.clearAllTasks() },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("Clear All")
            }
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector =
                        if(isDarkTheme)
                            Icons.Default.LightMode
                    else
                            Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme"
                )
            }
        }
    }
}