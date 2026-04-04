package com.kunvarpreet.to_dolist.ui.screens

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kunvarpreet.to_dolist.ui.components.TaskItem
import com.kunvarpreet.to_dolist.viewmodel.TaskViewModel
import java.util.Calendar

@Composable
fun TaskListScreen(viewModel: TaskViewModel, padding: PaddingValues) {
    val tasks by viewModel.tasks.collectAsState()
    val listState = rememberLazyListState()
    val now = System.currentTimeMillis()
    val today = getTodayDate()
    val todayCalendar = Calendar.getInstance()
    todayCalendar.set(Calendar.HOUR_OF_DAY, 0)
    todayCalendar.set(Calendar.MINUTE, 0)
    todayCalendar.set(Calendar.SECOND, 0)
    todayCalendar.set(Calendar.MILLISECOND, 0)
    val todayStart = todayCalendar.timeInMillis
    todayCalendar.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrowStart = todayCalendar.timeInMillis
    val sortedTasks = tasks.sortedBy { it.dateMillis ?: Long.MAX_VALUE }
    val overdueTasks = sortedTasks.filter {
        it.dateMillis != null && it.dateMillis < now
    }
    val todayTasksRaw = sortedTasks.filter {
        it.dateMillis != null &&
                it.dateMillis >= now &&
                it.dateMillis < tomorrowStart
    }
    val upcomingTasksRaw = sortedTasks.filter {
        it.dateMillis != null && it.dateMillis >= tomorrowStart
    }
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
            return@Column
        }
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
                                }
                            )
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

fun getTodayDate(): String {
    val cal = Calendar.getInstance()
    return "${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.YEAR)}"
}
