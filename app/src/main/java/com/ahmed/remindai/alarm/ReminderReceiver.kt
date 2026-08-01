package com.ahmed.remindai.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val id = intent.getIntExtra("id", 0)

        val title = intent.getStringExtra("title")
            ?: "Reminder"

        val body = intent.getStringExtra("body")
            ?: ""

        NotificationHelper.showNotification(
            context = context,
            notificationId = id,
            title = title,
            body = body
        )
    }
}
