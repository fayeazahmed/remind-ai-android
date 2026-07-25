package com.ahmed.remindai.repository

import com.ahmed.remindai.auth.TokenManager
import com.ahmed.remindai.network.AuthApi
import com.ahmed.remindai.network.LoginRequest
import com.ahmed.remindai.network.RegisterRequest

class AuthRepository(
    private val api: AuthApi,
    private val tokenManager: TokenManager
) {
    suspend fun register(email: String, password: String) {
        api.register(RegisterRequest(email, password))
    }

    suspend fun login(email: String, password: String) {
        val response = api.login(LoginRequest(email, password))
        tokenManager.saveToken(response.accessToken)
    }

    suspend fun logout() {
        tokenManager.clearToken()
    }
}
