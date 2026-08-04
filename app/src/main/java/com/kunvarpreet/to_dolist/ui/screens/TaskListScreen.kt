package com.kunvarpreet.to_dolist.ui.screens

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kunvarpreet.to_dolist.data.Task
import com.kunvarpreet.to_dolist.ui.components.EditTaskSheet
import com.kunvarpreet.to_dolist.ui.components.TaskItem
import com.kunvarpreet.to_dolist.viewmodel.TaskViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(viewModel: TaskViewModel, padding: PaddingValues) {
    val tasks by viewModel.tasks.collectAsState()
    val listState = rememberLazyListState()
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var isCompletedExpanded by rememberSaveable { mutableStateOf(false) }
    val editSheetState = rememberModalBottomSheetState()
    val now = System.currentTimeMillis()
    val todayCalendar = Calendar.getInstance()
    todayCalendar.set(Calendar.HOUR_OF_DAY, 0)
    todayCalendar.set(Calendar.MINUTE, 0)
    todayCalendar.set(Calendar.SECOND, 0)
    todayCalendar.set(Calendar.MILLISECOND, 0)
    todayCalendar.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrowStart = todayCalendar.timeInMillis

    val activeTasks = tasks.filter { !it.isDone }
    val completedTasks = tasks.filter { it.isDone }

    val sortedActiveTasks = activeTasks.sortedBy { it.dateMillis ?: Long.MAX_VALUE }
    val overdueTasks = sortedActiveTasks.filter {
        it.dateMillis != null && it.dateMillis < now
    }
    val todayTasksRaw = sortedActiveTasks.filter {
        it.dateMillis != null &&
                it.dateMillis >= now &&
                it.dateMillis < tomorrowStart
    }
    val upcomingTasksRaw = sortedActiveTasks.filter {
        it.dateMillis != null && it.dateMillis >= tomorrowStart
    }
    val unscheduledTasks = sortedActiveTasks.filter { it.dateMillis == null }

    val todayTasks = todayTasksRaw.sortedBy { it.dateMillis ?: Long.MAX_VALUE }
    val upcomingTasks = upcomingTasksRaw.sortedBy { it.dateMillis ?: Long.MAX_VALUE }

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        Text(
            text = "Tasks",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        if (tasks.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "You're all clear",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "Add a task to get started",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 80.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (overdueTasks.isNotEmpty()) {
                        item {
                            SectionHeader("Overdue")
                        }

                        items(overdueTasks, key = { it.id }) { task ->
                            androidx.compose.animation.AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                TaskItem(
                                    task = task,
                                    onDelete = { viewModel.deleteTask(task) },
                                    onCheckedChange = {
                                        viewModel.toggleTask(task, it)
                                    },
                                    onEdit = { taskToEdit = task },
                                    onReminderToggle = {
                                        viewModel.toggleReminder(task, it)
                                    }
                                )
                            }
                        }
                    }
                    if (todayTasks.isNotEmpty()) {
                        item {
                            SectionHeader("Today")
                        }
                        items(todayTasks, key = { it.id }) { task ->
                            androidx.compose.animation.AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                TaskItem(
                                    task = task,
                                    onDelete = { viewModel.deleteTask(task) },
                                    onCheckedChange = {
                                        viewModel.toggleTask(task, it)
                                    },
                                    onEdit = { taskToEdit = task },
                                    onReminderToggle = {
                                        viewModel.toggleReminder(task, it)
                                    }
                                )
                            }
                        }
                    }
                    if (upcomingTasks.isNotEmpty()) {
                        item {
                            SectionHeader("Upcoming")
                        }
                        items(upcomingTasks, key = { it.id }) { task ->
                            androidx.compose.animation.AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                TaskItem(
                                    task = task,
                                    onDelete = { viewModel.deleteTask(task) },
                                    onCheckedChange = {
                                        viewModel.toggleTask(task, it)
                                    },
                                    onEdit = { taskToEdit = task },
                                    onReminderToggle = {
                                        viewModel.toggleReminder(task, it)
                                    }
                                )
                            }
                        }
                    }
                    if (unscheduledTasks.isNotEmpty()) {
                        item {
                            SectionHeader("No Due Date")
                        }
                        items(unscheduledTasks, key = { it.id }) { task ->
                            androidx.compose.animation.AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                TaskItem(
                                    task = task,
                                    onDelete = { viewModel.deleteTask(task) },
                                    onCheckedChange = {
                                        viewModel.toggleTask(task, it)
                                    },
                                    onEdit = { taskToEdit = task },
                                    onReminderToggle = {
                                        viewModel.toggleReminder(task, it)
                                    }
                                )
                            }
                        }
                    }
                    if (completedTasks.isNotEmpty()) {
                        item {
                            CompletedSectionHeader(
                                count = completedTasks.size,
                                isExpanded = isCompletedExpanded,
                                onToggleExpand = { isCompletedExpanded = !isCompletedExpanded }
                            )
                        }
                        if (isCompletedExpanded) {
                            items(completedTasks, key = { it.id }) { task ->
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn() + scaleIn(),
                                    exit = fadeOut() + scaleOut()
                                ) {
                                    TaskItem(
                                        task = task,
                                        onDelete = { viewModel.deleteTask(task) },
                                        onCheckedChange = {
                                            viewModel.toggleTask(task, it)
                                        },
                                        onEdit = { taskToEdit = task },
                                        onReminderToggle = {
                                            viewModel.toggleReminder(task, it)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                            )
                        )
                )
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

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun CompletedSectionHeader(
    count: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleExpand)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Completed",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "($count)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (isExpanded) "Collapse completed tasks" else "Expand completed tasks",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

