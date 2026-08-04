package com.kunvarpreet.to_dolist.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import com.kunvarpreet.to_dolist.data.Task

class TaskAlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun schedule(task: Task) {
        val triggerTime = task.timeMillis ?: task.dateMillis
        if (triggerTime == null || !task.hasReminder || task.isDone) {
            cancel(task)
            return
        }
        val now = System.currentTimeMillis()
        val fiveMinBeforeTime = triggerTime - (5 * 60 * 1000L)
        // 1. Schedule 5-minute advance alarm if in future
        if (fiveMinBeforeTime > now) {
            scheduleAlarm(
                requestCode = task.id * 2 + 1,
                triggerTime = fiveMinBeforeTime,
                taskId = task.id,
                title = task.title,
                isEarly = true
            )
        } else {
            cancelAlarm(task.id * 2 + 1)
        }

        // 2. Schedule exact due-time alarm if in future
        if (triggerTime > now) {
            scheduleAlarm(
                requestCode = task.id * 2,
                triggerTime = triggerTime,
                taskId = task.id,
                title = task.title,
                isEarly = false
            )
        } else {
            cancelAlarm(task.id * 2)
        }
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private fun scheduleAlarm(
        requestCode: Int,
        triggerTime: Long,
        taskId: Int,
        title: String,
        isEarly: Boolean
    ) {
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra(TaskReminderReceiver.EXTRA_TASK_ID, taskId)
            putExtra(TaskReminderReceiver.EXTRA_TASK_TITLE, title)
            putExtra(TaskReminderReceiver.EXTRA_IS_EARLY, isEarly)
            putExtra(TaskReminderReceiver.EXTRA_NOTIFICATION_ID, requestCode)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancel(task: Task) {
        cancelAlarm(task.id * 2)
        cancelAlarm(task.id * 2 + 1)
    }

    private fun cancelAlarm(requestCode: Int) {
        val intent = Intent(context, TaskReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
