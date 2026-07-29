package com.ahmed.remindai.repository

import com.ahmed.remindai.model.Reminder
import com.ahmed.remindai.network.CreateReminderRequest
import com.ahmed.remindai.network.ReminderApi
import com.ahmed.remindai.network.ReminderDto
import com.ahmed.remindai.network.SearchQuery
import com.ahmed.remindai.network.SearchReminderDto
import com.ahmed.remindai.network.SummarizeRequest
import com.ahmed.remindai.network.UpdateReminderRequest

class ReminderRepository(
    private val api: ReminderApi
) {

    suspend fun getReminders(): List<Reminder> =
        api.getReminders().map { it.toUiModel() }

    suspend fun addReminder(text: String): Reminder =
        api.createReminder(CreateReminderRequest(text)).toUiModel()

    suspend fun updateReminder(
        id: Int,
        title: String,
        body: String,
        notifyAt: String?,
        priority: Int
    ): Reminder =
        api.updateReminder(
            id,
            UpdateReminderRequest(
                title = title,
                body = body,
                notifyAt = notifyAt,
                priority = priority
            )
        ).toUiModel()

    suspend fun deleteReminder(id: Int) {
        api.deleteReminder(id)
    }

    suspend fun askAi(query: String): String {

        return if (shouldSummarize(query)) {

            api.summarizeReminders(
                SummarizeRequest(
                    query = query
                )
            ).summary

        } else {

            val results = api.searchReminders(
                SearchQuery(
                    query = query
                )
            )

            formatSearchResults(results)
        }
    }

    private fun shouldSummarize(query: String): Boolean {

        val text = query.lowercase()

        val keywords = listOf(
            "summary",
            "summarize",
            "summarise",
            "overview",
            "recap",
            "everything",
            "all reminders",
            "all my reminders"
        )

        return keywords.any { text.contains(it) }
    }

    private fun formatSearchResults(
        results: List<SearchReminderDto>
    ): String {

        if (results.isEmpty()) {
            return "I couldn't find any matching reminders."
        }

        return buildString {

            appendLine("I found ${results.size} matching reminder(s):")
            appendLine()

            results.forEachIndexed { index, reminder ->

                append("${index + 1}. ")
                append(reminder.title)

                if (reminder.notifyAt != null) {
                    append(" (${reminder.notifyAt})")
                }

                appendLine()

                if (reminder.body.isNotBlank()) {
                    appendLine(reminder.body)
                }

                appendLine()
            }
        }.trim()
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
