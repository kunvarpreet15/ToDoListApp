package com.kunvarpreet.to_dolist
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kunvarpreet.to_dolist.ui.TodoScreen
import com.kunvarpreet.to_dolist.ui.theme.ToDoListTheme
import com.kunvarpreet.to_dolist.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkTheme by remember { mutableStateOf(false) }
            ToDoListTheme(
                darkTheme = darkTheme
            ) {
                TodoScreen(
                    viewModel = viewModel,
                    onToggleTheme = { darkTheme = !darkTheme }
                )
            }
        }
    }
}
