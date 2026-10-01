package com.omniroute.app.data.model

data class RoutingRule(
    val id: String,
    val modelPattern: String,
    val primaryProvider: String,
    val fallbackProviders: List<String>,
    val loadBalancingStrategy: LoadBalancingStrategy = LoadBalancingStrategy.PRIORITY_FALLBACK,
    val retryOnHttpCodes: List<Int> = listOf(429, 500, 502, 503, 504),
    val timeoutMs: Long = 30000,
    val isEnabled: Boolean = true
)

enum class LoadBalancingStrategy {
    PRIORITY_FALLBACK,
    ROUND_ROBIN,
    LOWEST_LATENCY,
    LOWEST_COST
}
