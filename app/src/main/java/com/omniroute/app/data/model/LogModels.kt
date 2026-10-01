package com.omniroute.app.data.model

import java.util.UUID

data class RequestLog(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val endpoint: String = "/v1/chat/completions",
    val requestedModel: String,
    val resolvedProvider: String,
    val statusCode: Int = 200,
    val latencyMs: Long,
    val promptTokens: Int,
    val completionTokens: Int,
    val tokensSavedPercent: Int,
    val fallbackOccurred: Boolean = false,
    val fallbackChain: List<String> = emptyList(),
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)
