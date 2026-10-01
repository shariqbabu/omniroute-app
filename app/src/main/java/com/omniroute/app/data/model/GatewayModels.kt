package com.omniroute.app.data.model

data class GatewayStats(
    val isOnline: Boolean = true,
    val serverVersion: String = "v3.8.51",
    val activeProvidersCount: Int = 18,
    val totalProvidersAvailable: Int = 359,
    val totalRequests: Long = 14280,
    val tokensProcessed: Long = 8954200,
    val tokensSavedTokens: Long = 4120300,
    val averageTokenSavingsPercent: Int = 46,
    val avgLatencyMs: Long = 340,
    val uptimeHours: Double = 98.4,
    val rtkCompressionEnabled: Boolean = true,
    val cavemanModeEnabled: Boolean = true,
    val estimatedCostSavedUsd: Double = 124.65
)

data class AppSettings(
    val serverUrl: String = "http://10.0.2.2:3000",
    val masterApiKey: String = "",
    val activeModel: String = "claude-3-5-sonnet",
    val rtkCompressionEnabled: Boolean = true,
    val cavemanMode: Boolean = true,
    val autoFallbackEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.AMOLED_DARK,
    val temperature: Float = 0.7f,
    val systemPrompt: String = "You are an intelligent AI assistant powered by OmniRoute Gateway."
)

enum class ThemeMode {
    AMOLED_DARK, SYSTEM_DEFAULT, LIGHT
}
