package com.kunvarpreet.to_dolist.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.kunvarpreet.to_dolist.data.Task
import java.util.Calendar

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskItem(
    task: Task,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()
    val dateText = formatDate(task.dateMillis)
    val timeText = formatTime(task.timeMillis)
    val todayCalendar = Calendar.getInstance()
    val now = System.currentTimeMillis()
    todayCalendar.set(Calendar.HOUR_OF_DAY, 0)
    todayCalendar.set(Calendar.MINUTE, 0)
    todayCalendar.set(Calendar.SECOND, 0)
    todayCalendar.set(Calendar.MILLISECOND, 0)
    val todayStart = todayCalendar.timeInMillis
    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val progress = dismissState.progress
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = lerp(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.errorContainer,
                            progress
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.alpha(progress).scale(0.8f + (0.4f * progress))
                )
            }
        },
        content = {
            task.dateMillis?.let {
                Card(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    ),
                    colors = CardDefaults.cardColors(
                        if (it < now) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                    modifier = modifier
                        .fillMaxWidth()
                        .alpha(if (task.isDone) 0.5f else 1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isDone,
                            onCheckedChange = onCheckedChange
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleMedium,
                                textDecoration =
                                    if (task.isDone)
                                        TextDecoration.LineThrough
                                    else
                                        TextDecoration.None
                            )
                            if (dateText.isNotEmpty() || timeText.isNotEmpty()) {
                                Text("$dateText $timeText")
                            }
                        }
                    }
                }
            }
        }
    )
    if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
        onDelete()
    }
}