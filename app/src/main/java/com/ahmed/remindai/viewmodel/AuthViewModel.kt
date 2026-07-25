package com.ahmed.remindai.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.remindai.auth.TokenHolder
import com.ahmed.remindai.network.RetrofitInstance
import com.ahmed.remindai.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository(
        RetrofitInstance.authApi,
        RetrofitInstance.tokenManager
    )
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = RetrofitInstance.tokenManager.tokenFlow
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TokenHolder.token != null
        )

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.login(email.trim(), password)
            } catch (e: Exception) {
                errorMessage = "Login failed: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.register(email.trim(), password)
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Registration failed: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
