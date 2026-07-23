package com.ahmed.remindai.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReminderApi {
    @GET("reminders")
    suspend fun getReminders(): List<ReminderDto>

    @POST("reminders")
    suspend fun createReminder(@Body request: CreateReminderRequest): ReminderDto

    @DELETE("reminders/{id}")
    suspend fun deleteReminder(@Path("id") id: Int)
}
