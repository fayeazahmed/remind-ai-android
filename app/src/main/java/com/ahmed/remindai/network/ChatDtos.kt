package com.ahmed.remindai.network

import com.google.gson.annotations.SerializedName

data class SearchQuery(

    @SerializedName("query")
    val query: String,

    @SerializedName("top_k")
    val topK: Int = 5
)

data class SummarizeRequest(

    @SerializedName("reminder_ids")
    val reminderIds: List<Int>? = null,

    @SerializedName("query")
    val query: String? = null
)

data class SearchReminderDto(

    val id: Int,

    val score: Double,

    val title: String,

    val body: String,

    val priority: Int,

    val notifyAt: String?
)

data class SummaryResponse(

    val summary: String
)
