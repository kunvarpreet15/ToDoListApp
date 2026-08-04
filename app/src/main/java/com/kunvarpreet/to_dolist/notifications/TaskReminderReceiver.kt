package com.kunvarpreet.to_dolist.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)
        val title = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Task Reminder"
        val isEarly = intent.getBooleanExtra(EXTRA_IS_EARLY, false)
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, taskId)
        if (taskId != -1) {
            NotificationHelper.showNotification(context, notificationId, title, isEarly)
        }
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_IS_EARLY = "extra_is_early"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }
}
