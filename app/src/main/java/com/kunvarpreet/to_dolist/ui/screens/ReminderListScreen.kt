package com.kunvarpreet.to_dolist.ui.screens

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kunvarpreet.to_dolist.data.Task
import com.kunvarpreet.to_dolist.ui.components.EditTaskSheet
import com.kunvarpreet.to_dolist.ui.components.TaskItem
import com.kunvarpreet.to_dolist.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListScreen(viewModel: TaskViewModel, padding: PaddingValues) {
    val tasks by viewModel.tasks.collectAsState()
    val reminderTasks = remember(tasks) {
        tasks.filter { it.hasReminder }.sortedBy { it.dateMillis ?: Long.MAX_VALUE }
    }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    val editSheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        Text(
            text = "Reminders",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        if (reminderTasks.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "No reminders set",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "Enable reminder toggle when adding or editing a task",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reminderTasks, key = { it.id }) { task ->
                    androidx.compose.animation.AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        TaskItem(
                            task = task,
                            onDelete = { viewModel.deleteTask(task) },
                            onCheckedChange = { viewModel.toggleTask(task, it) },
                            onEdit = { taskToEdit = task },
                            onReminderToggle = { viewModel.toggleReminder(task, it) }
                        )
                    }
                }
            }
        }
    }

    taskToEdit?.let { currentTask ->
        ModalBottomSheet(
            onDismissRequest = { taskToEdit = null },
            sheetState = editSheetState
        ) {
            EditTaskSheet(
                task = currentTask,
                onSave = { updatedTitle, updatedDate, updatedTime, updatedHasReminder ->
                    viewModel.updateTask(
                        currentTask,
                        updatedTitle,
                        updatedDate,
                        updatedTime,
                        updatedHasReminder
                    )
                    taskToEdit = null
                }
            )
        }
    }
}