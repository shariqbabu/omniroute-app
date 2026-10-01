package com.omniroute.app.data.remote

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

interface OmniRouteApiService {

    @GET("/health")
    suspend fun checkHealth(): Response<JsonObject>

    @GET("/v1/models")
    suspend fun listModels(
        @Header("Authorization") authHeader: String?
    ): Response<JsonObject>

    @GET("/api/stats")
    suspend fun getStats(
        @Header("Authorization") authHeader: String?
    ): Response<JsonObject>

    @GET("/api/providers")
    suspend fun getProviders(
        @Header("Authorization") authHeader: String?
    ): Response<JsonObject>

    @POST("/api/providers/{id}/toggle")
    suspend fun toggleProvider(
        @Path("id") providerId: String,
        @Header("Authorization") authHeader: String?,
        @Body body: JsonObject
    ): Response<JsonObject>

    @GET("/api/logs")
    suspend fun getLogs(
        @Header("Authorization") authHeader: String?,
        @Query("limit") limit: Int = 50
    ): Response<JsonObject>
}
