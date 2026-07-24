package com.ahmed.remindai.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.remindai.network.RetrofitInstance
import com.ahmed.remindai.repository.ReminderRepository
import kotlinx.coroutines.launch

class AiChatViewModel(
    private val repository: ReminderRepository =
        ReminderRepository(RetrofitInstance.api)
) : ViewModel() {

    var response by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun ask(query: String) {

        val trimmed = query.trim()

        if (trimmed.isBlank()) return

        viewModelScope.launch {

            isLoading = true
            errorMessage = null
            response = ""

            try {

                response = repository.askAi(trimmed)

            } catch (e: Exception) {

                errorMessage = e.message ?: "Unknown error"

            } finally {

                isLoading = false
            }
        }
    }
}
