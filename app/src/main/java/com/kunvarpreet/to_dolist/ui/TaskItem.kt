package com.kunvarpreet.to_dolist.ui
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.kunvarpreet.to_dolist.data.Task

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskItem(
    task: Task,
    onDelete: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val progress = dismissState.progress
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.alpha(progress)
                )
            }
        },
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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
                    textDecoration =
                        if (task.isDone)
                            TextDecoration.LineThrough
                        else
                            TextDecoration.None
                )
                        if (task.date.isNotEmpty() || task.time.isNotEmpty()) {
                    Text("${task.date} ${task.time}")
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