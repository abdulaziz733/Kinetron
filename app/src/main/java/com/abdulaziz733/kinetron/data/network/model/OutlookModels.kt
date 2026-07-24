package com.abdulaziz733.kinetron.data.network.model

data class OutlookMessagesResponse(
    val value: List<OutlookMessage>?
)

data class OutlookMessage(
    val id: String,
    val subject: String?,
    val bodyPreview: String?,
    val body: OutlookBody?,
    val receivedDateTime: String?, // ISO 8601 string, e.g. "2026-07-20T11:00:00Z"
    val from: OutlookFromRecipient?
)

data class OutlookBody(
    val contentType: String?, // "text" or "html"
    val content: String?
)

data class OutlookFromRecipient(
    val emailAddress: OutlookEmailAddress?
)

data class OutlookEmailAddress(
    val name: String?,
    val address: String?
)
