package com.ahmed.remindai.repository

import com.ahmed.remindai.model.Reminder
import com.ahmed.remindai.network.CreateReminderRequest
import com.ahmed.remindai.network.ReminderApi
import com.ahmed.remindai.network.ReminderDto

class ReminderRepository(private val api: ReminderApi) {

    suspend fun getReminders(): List<Reminder> =
        api.getReminders().map { it.toUiModel() }

    suspend fun addReminder(text: String): Reminder =
        api.createReminder(CreateReminderRequest(text)).toUiModel()

    suspend fun deleteReminder(id: Int) {
        api.deleteReminder(id)
    }

    private fun ReminderDto.toUiModel() = Reminder(
        id = id,
        title = title,
        body = body,
        priority = priority,
        done = done,
        notifyAt = notifyAt,
        createdAt = createdAt
    )
}
