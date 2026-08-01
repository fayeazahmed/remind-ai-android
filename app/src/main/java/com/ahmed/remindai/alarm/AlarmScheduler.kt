package com.ahmed.remindai.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ahmed.remindai.model.Reminder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import android.os.Build
import android.util.Log

object AlarmScheduler {

    fun schedule(
        context: Context,
        reminder: Reminder
    ) {

        val notifyAt = reminder.notifyAt ?: return

        val triggerMillis = parseTime(notifyAt) ?: return

        if (triggerMillis <= System.currentTimeMillis()) {
            return
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("id", reminder.id)
            putExtra("title", reminder.title)
            putExtra("body", reminder.body)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms()
        ) {
            return
        }

        Log.i(
                "AlarmScheduler",
                """
        Scheduling reminder:
        title=${reminder.title}
        notifyAt=${reminder.notifyAt}
        triggerMillis=$triggerMillis
        triggerDate=${java.util.Date(triggerMillis)}
        """.trimIndent()
            )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerMillis,
            pendingIntent
        )
    }

    fun cancel(
        context: Context,
        reminderId: Int
    ) {

        val intent = Intent(context, ReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)
    }

    private fun parseTime(value: String): Long? {

        return try {

            Instant.parse(value).toEpochMilli()

        } catch (_: Exception) {

            try {

                LocalDateTime
                    .parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

            } catch (_: Exception) {

                null
            }
        }
    }
}
