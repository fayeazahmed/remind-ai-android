package com.ahmed.remindai.network

import com.google.gson.annotations.SerializedName

data class ReminderDto(
    val id: Int,
    val title: String,
    val body: String,
    val priority: Int,
    val done: Boolean,
    val notifyAt: String?,
    val createdAt: String
)

data class CreateReminderRequest(
    @SerializedName("raw_text")
    val rawText: String
)

data class UpdateReminderRequest(
    val title: String,
    val body: String,
    val notifyAt: String?,
    val priority: Int
)
