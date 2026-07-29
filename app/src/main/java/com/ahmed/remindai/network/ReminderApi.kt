package com.ahmed.remindai.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface ReminderApi {

    @GET("reminders")
    suspend fun getReminders(): List<ReminderDto>

    @POST("reminders")
    suspend fun createReminder(
        @Body request: CreateReminderRequest
    ): ReminderDto

    @PUT("reminders/{id}")
    suspend fun updateReminder(
        @Path("id") id: Int,
        @Body request: UpdateReminderRequest
    ): ReminderDto

    @DELETE("reminders/{id}")
    suspend fun deleteReminder(
        @Path("id") id: Int
    )

    @POST("reminders/search")
    suspend fun searchReminders(
        @Body request: SearchQuery
    ): List<SearchReminderDto>

    @POST("reminders/summarize")
    suspend fun summarizeReminders(
        @Body request: SummarizeRequest
    ): SummaryResponse
}
