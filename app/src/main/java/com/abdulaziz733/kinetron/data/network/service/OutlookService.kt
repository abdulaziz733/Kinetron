package com.abdulaziz733.kinetron.data.network.service

import com.abdulaziz733.kinetron.data.network.model.OutlookMessagesResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface OutlookService {
    @GET("v1.0/me/messages")
    suspend fun getMessages(
        @Header("Authorization") authHeader: String,
        @Query("\$filter") filter: String? = null // e.g. "receivedDateTime gt 2026-07-20T11:00:00Z"
    ): OutlookMessagesResponse
}
