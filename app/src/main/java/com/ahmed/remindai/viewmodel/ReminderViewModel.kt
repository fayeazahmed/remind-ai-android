package com.ahmed.remindai.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        loadReminders()
    }

    fun loadReminders() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                reminders = repository.getReminders()
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
            } catch (e: Exception) {
                errorMessage = "Failed to add reminder: ${e.message}"
            }
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            errorMessage = null
            try {
                repository.deleteReminder(reminder.id)
                reminders = reminders.filterNot { it.id == reminder.id }
            } catch (e: Exception) {
                errorMessage = "Failed to delete reminder: ${e.message}"
            }
        }
    }
}