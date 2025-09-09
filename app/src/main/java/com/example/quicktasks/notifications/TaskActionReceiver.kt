package com.example.quicktasks.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Broadcast receiver for handling task action notifications
 */
class TaskActionReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "COMPLETE_TASK" -> {
                val taskId = intent.getLongExtra("taskId", -1)
                if (taskId != -1L) {
                    // Handle task completion
                    // This would typically update the database and cancel notifications
                    val notificationHelper = NotificationHelper(context)
                    notificationHelper.cancelTaskNotification(taskId)
                }
            }
            "SNOOZE_TASK" -> {
                val taskId = intent.getLongExtra("taskId", -1)
                if (taskId != -1L) {
                    // Handle task snoozing
                    // This would typically reschedule the notification for later
                }
            }
        }
    }
}
