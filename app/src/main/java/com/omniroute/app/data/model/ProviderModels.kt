package com.omniroute.app.data.model

data class AiModel(
    val id: String,
    val name: String,
    val provider: String,
    val contextWindow: Int = 128000,
    val isFree: Boolean = false,
    val supportsVision: Boolean = true,
    val supportsTools: Boolean = true,
    val supportsReasoning: Boolean = false,
    val costPer1kInput: Double = 0.0,
    val costPer1kOutput: Double = 0.0
)

data class AiProvider(
    val id: String,
    val name: String,
    val category: ProviderCategory,
    val isEnabled: Boolean = true,
    val apiKey: String = "",
    val baseUrl: String = "",
    val isFreeTier: Boolean = false,
    val priority: Int = 1,
    val status: ProviderStatus = ProviderStatus.ACTIVE,
    val latencyMs: Long = 0,
    val availableModels: List<String> = emptyList()
)

enum class ProviderCategory {
    TIER_ONE,        // Anthropic, OpenAI, Google
    FREE_TIER,       // 150+ Free Community Providers
    CLOUD_AGGREGATOR,// Groq, OpenRouter, Together AI, Mistral, Cerebras
    LOCAL_INFERENCE, // Ollama, LMStudio, vLLM
    CHINESE_MODELS   // DeepSeek, Moonshot/Kimi, Qwen, GLM, MiniMax
}

enum class ProviderStatus {
    ACTIVE, RATE_LIMITED, ERROR, DISABLED
}
