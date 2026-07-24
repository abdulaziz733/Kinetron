package com.abdulaziz733.kinetron.data.network.service

import com.abdulaziz733.kinetron.data.network.model.GmailMessageDetail
import com.abdulaziz733.kinetron.data.network.model.GmailMessagesResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GmailService {
    @GET("gmail/v1/users/me/messages")
    suspend fun getMessages(
        @Header("Authorization") authHeader: String,
        @Query("q") query: String? = null // e.g., "after:1712345678"
    ): GmailMessagesResponse

    @GET("gmail/v1/users/me/messages/{id}")
    suspend fun getMessageDetail(
        @Header("Authorization") authHeader: String,
        @Path("id") messageId: String
    ): GmailMessageDetail
}
