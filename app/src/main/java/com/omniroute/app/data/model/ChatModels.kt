package com.omniroute.app.data.model

import java.util.UUID

enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole,
    val content: String,
    val modelName: String? = null,
    val providerUsed: String? = null,
    val latencyMs: Long? = null,
    val tokenSavingsPercent: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatCompletionRequest(
    val model: String,
    val messages: List<ApiMessage>,
    val stream: Boolean = true,
    val temperature: Float = 0.7f,
    val max_tokens: Int? = null
)

data class ApiMessage(
    val role: String,
    val content: String
)
