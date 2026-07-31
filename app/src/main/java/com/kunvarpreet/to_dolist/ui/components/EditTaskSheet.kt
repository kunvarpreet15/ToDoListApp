package com.kunvarpreet.to_dolist.ui.components

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kunvarpreet.to_dolist.data.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun EditTaskSheet(
    task: Task,
    onSave: (String, Long?, Long?) -> Unit
) {
    var taskText by remember(task) { mutableStateOf(task.title) }
    val selectedDateMillis = remember(task) { mutableStateOf<Long?>(task.dateMillis) }
    val selectedTimeMillis = remember(task) { mutableStateOf<Long?>(task.timeMillis) }
    val context = LocalContext.current

    val initialCalendar = Calendar.getInstance().apply {
        (task.timeMillis ?: task.dateMillis)?.let {
            timeInMillis = it
        }
    }

    val selectedDate = selectedDateMillis.value?.let {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
    } ?: ""
    val selectedTime = selectedTimeMillis.value?.let {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it)
    } ?: ""

    val datePicker = remember(task) {
        android.app.DatePickerDialog(
            context,
            { _, year, month, day ->
                val cal = Calendar.getInstance()
                selectedDateMillis.value?.let { cal.timeInMillis = it }
                cal.set(year, month, day, 0, 0, 0)
                cal.set(Calendar.MILLISECOND, 0)
                selectedDateMillis.value = cal.timeInMillis
            },
            initialCalendar.get(Calendar.YEAR),
            initialCalendar.get(Calendar.MONTH),
            initialCalendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val timePicker = remember(task) {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val cal = Calendar.getInstance()
                selectedDateMillis.value?.let {
                    cal.timeInMillis = it
                }
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                selectedTimeMillis.value = cal.timeInMillis
            },
            initialCalendar.get(Calendar.HOUR_OF_DAY),
            initialCalendar.get(Calendar.MINUTE),
            false
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Edit Task", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        TextField(
            value = taskText,
            onValueChange = { taskText = it },
            placeholder = { Text("Task title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePicker.show() }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Date")
            Text(selectedDate.ifEmpty { "Select" })
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { timePicker.show() }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Time")
            Text(selectedTime.ifEmpty { "Select" })
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (taskText.isNotBlank()) {
                    onSave(
                        taskText,
                        selectedDateMillis.value,
                        selectedTimeMillis.value
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}
