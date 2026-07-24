package com.abdulaziz733.kinetron.data.network.model

data class GmailMessagesResponse(
    val messages: List<GmailMessageSummary>?,
    val nextPageToken: String?,
    val resultSizeEstimate: Int?
)

data class GmailMessageSummary(
    val id: String,
    val threadId: String
)

data class GmailMessageDetail(
    val id: String,
    val threadId: String,
    val snippet: String?,
    val payload: GmailPayload?,
    val internalDate: Long // Epoch ms
)

data class GmailPayload(
    val headers: List<GmailHeader>?,
    val mimeType: String?,
    val body: GmailPartBody?,
    val parts: List<GmailPayloadPart>?
)

data class GmailPayloadPart(
    val partId: String?,
    val mimeType: String?,
    val filename: String?,
    val headers: List<GmailHeader>?,
    val body: GmailPartBody?,
    val parts: List<GmailPayloadPart>?
)

data class GmailPartBody(
    val size: Int?,
    val data: String? // Base64url encoded body content
)

data class GmailHeader(
    val name: String,
    val value: String
)
