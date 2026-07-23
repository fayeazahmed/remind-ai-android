package com.ahmed.remindai.model

data class Reminder(
    val id: Int,
    val title: String,
    val body: String,
    val priority: Int,
    val done: Boolean,
    val notifyAt: String?,
    val createdAt: String
)
