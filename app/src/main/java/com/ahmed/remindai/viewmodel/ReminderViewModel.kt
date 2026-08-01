package com.ahmed.remindai.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.remindai.RemindAIApplication
import com.ahmed.remindai.alarm.AlarmScheduler
import com.ahmed.remindai.model.Reminder
import com.ahmed.remindai.network.RetrofitInstance
import com.ahmed.remindai.repository.ReminderRepository
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val repository: ReminderRepository = ReminderRepository(RetrofitInstance.api)
) : ViewModel() {

    companion object {
        private const val TAG = "ReminderViewModel"
    }

    var reminders by mutableStateOf<List<Reminder>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var newlyCreatedReminder by mutableStateOf<Reminder?>(null)
        private set

    var isSavingReminder by mutableStateOf(false)
        private set

    init {
        loadReminders()
    }

    fun loadReminders() {
        viewModelScope.launch {

            isLoading = true
            errorMessage = null

            try {

                reminders = repository.getReminders()

                reminders.forEach { reminder ->
                    if (!reminder.done && reminder.notifyAt != null) {
                        AlarmScheduler.schedule(
                            RemindAIApplication.appContext,
                            reminder
                        )
                    }
                }

                Log.d(TAG, "Reminders loaded: $reminders")

            } catch (e: Exception) {

                Log.e(TAG, "Failed to load reminders", e)
                errorMessage = "Failed to load reminders: ${e.message}"

            } finally {
                isLoading = false
            }
        }
    }

    fun addReminder(text: String) {

        val trimmed = text.trim()

        if (trimmed.isEmpty()) return

        viewModelScope.launch {

            errorMessage = null

            try {

                val newReminder = repository.addReminder(trimmed)

                reminders = listOf(newReminder) + reminders
                newlyCreatedReminder = newReminder

                if (!newReminder.done && newReminder.notifyAt != null) {
                    AlarmScheduler.schedule(
                        RemindAIApplication.appContext,
                        newReminder
                    )
                }

            } catch (e: Exception) {

                errorMessage = "Failed to add reminder: ${e.message}"
            }
        }
    }

    fun dismissNewReminderDialog() {
        newlyCreatedReminder = null
    }

    fun saveEditedReminder(
        id: Int,
        title: String,
        body: String,
        notifyAt: String?,
        priority: Int
    ) {

        val existing = reminders.firstOrNull { it.id == id } ?: return

        updateReminder(
            existing.copy(
                title = title,
                body = body,
                notifyAt = notifyAt,
                priority = priority
            ),
            dismissDialog = true
        )
    }

    fun toggleReminderDone(
        reminder: Reminder,
        done: Boolean
    ) {

        updateReminder(
            reminder.copy(done = done)
        )
    }

    private fun updateReminder(
        reminder: Reminder,
        dismissDialog: Boolean = false
    ) {

        viewModelScope.launch {

            errorMessage = null
            isSavingReminder = true

            try {

                val updated = repository.updateReminder(reminder)

                reminders = reminders.map {
                    if (it.id == updated.id) updated else it
                }

                AlarmScheduler.cancel(
                    RemindAIApplication.appContext,
                    updated.id
                )

                if (!updated.done && updated.notifyAt != null) {
                    AlarmScheduler.schedule(
                        RemindAIApplication.appContext,
                        updated
                    )
                }

            } catch (e: Exception) {

                Log.e(TAG, "Failed to update reminder", e)
                errorMessage = "Failed to update reminder: ${e.message}"

            } finally {

                isSavingReminder = false

                if (dismissDialog) {
                    newlyCreatedReminder = null
                }
            }
        }
    }

    fun deleteReminder(reminder: Reminder) {

        viewModelScope.launch {

            errorMessage = null

            try {

                repository.deleteReminder(reminder.id)

                AlarmScheduler.cancel(
                    RemindAIApplication.appContext,
                    reminder.id
                )

                reminders = reminders.filterNot {
                    it.id == reminder.id
                }

            } catch (e: Exception) {

                errorMessage = "Failed to delete reminder: ${e.message}"
            }
        }
    }
}
