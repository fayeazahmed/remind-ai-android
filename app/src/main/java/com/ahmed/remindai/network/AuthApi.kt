package com.ahmed.remindai.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)

data class UserOut(
    val id: Int,
    val email: String
)

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String
)

interface AuthApi {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): UserOut

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse
}
