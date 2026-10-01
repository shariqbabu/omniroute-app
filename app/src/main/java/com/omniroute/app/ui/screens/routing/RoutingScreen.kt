package com.omniroute.app.ui.screens.routing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.omniroute.app.data.model.RoutingRule
import com.omniroute.app.ui.theme.*
import com.omniroute.app.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutingScreen(
    viewModel: MainViewModel
) {
    val routingRules by viewModel.routingRules.collectAsState()
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Smart Routing & Fallbacks",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OmniTextPrimary
                        )
                        Text(
                            text = "Auto-failover when rate-limited or 5xx",
                            style = MaterialTheme.typography.bodySmall,
                            color = OmniSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OmniBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(OmniBackground)
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                CompressorEngineCard(
                    rtk = settings.rtkCompressionEnabled,
                    caveman = settings.cavemanMode,
                    autoFallback = settings.autoFallbackEnabled,
                    onToggleRtk = {
                        viewModel.updateServerSettings(
                            url = settings.serverUrl,
                            apiKey = settings.masterApiKey,
                            rtk = !settings.rtkCompressionEnabled,
                            caveman = settings.cavemanMode,
                            autoFallback = settings.autoFallbackEnabled,
                            temp = settings.temperature,
                            systemPrompt = settings.systemPrompt
                        )
                    },
                    onToggleCaveman = {
                        viewModel.updateServerSettings(
                            url = settings.serverUrl,
                            apiKey = settings.masterApiKey,
                            rtk = settings.rtkCompressionEnabled,
                            caveman = !settings.cavemanMode,
                            autoFallback = settings.autoFallbackEnabled,
                            temp = settings.temperature,
                            systemPrompt = settings.systemPrompt
                        )
                    },
                    onToggleFallback = {
                        viewModel.updateServerSettings(
                            url = settings.serverUrl,
                            apiKey = settings.masterApiKey,
                            rtk = settings.rtkCompressionEnabled,
                            caveman = settings.cavemanMode,
                            autoFallback = !settings.autoFallbackEnabled,
                            temp = settings.temperature,
                            systemPrompt = settings.systemPrompt
                        )
                    }
                )
            }

            item {
                Text(
                    text = "Active Fallback Chains",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OmniTextPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(routingRules, key = { it.id }) { rule ->
                RoutingRuleCard(rule = rule)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CompressorEngineCard(
    rtk: Boolean,
    caveman: Boolean,
    autoFallback: Boolean,
    onToggleRtk: () -> Unit,
    onToggleCaveman: () -> Unit,
    onToggleFallback: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = OmniSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, OmniCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Optimization & Redundancy Engine",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OmniTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // RTK Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("RTK Compression", style = MaterialTheme.typography.bodyLarge, color = OmniTextPrimary)
                    Text("Removes redundant prompt tokens (15–40% save)", style = MaterialTheme.typography.bodySmall, color = OmniTextSecondary)
                }
                Switch(checked = rtk, onCheckedChange = { onToggleRtk() })
            }

            HorizontalDivider(color = OmniCardBorder, modifier = Modifier.padding(vertical = 10.dp))

            // Caveman Mode Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Caveman Mode", style = MaterialTheme.typography.bodyLarge, color = OmniTextPrimary)
                    Text("Ultra-compact token compression (up to 95% save)", style = MaterialTheme.typography.bodySmall, color = OmniTextSecondary)
                }
                Switch(checked = caveman, onCheckedChange = { onToggleCaveman() })
            }

            HorizontalDivider(color = OmniCardBorder, modifier = Modifier.padding(vertical = 10.dp))

            // Auto Fallback Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Auto-Fallback Chain", style = MaterialTheme.typography.bodyLarge, color = OmniTextPrimary)
                    Text("Reroutes on 429 RateLimit or 500 error seamlessly", style = MaterialTheme.typography.bodySmall, color = OmniTextSecondary)
                }
                Switch(checked = autoFallback, onCheckedChange = { onToggleFallback() })
            }
        }
    }
}

@Composable
fun RoutingRuleCard(rule: RoutingRule) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = OmniCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, OmniCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pattern: ${rule.modelPattern}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = OmniAccentCyan
                )
                Text(
                    text = rule.loadBalancingStrategy.name.replace("_", " "),
                    style = MaterialTheme.typography.labelSmall,
                    color = OmniTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "1. ${rule.primaryProvider}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = OmniSuccess
                )
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = OmniTextMuted,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(horizontal = 4.dp)
                )
                rule.fallbackProviders.forEachIndexed { idx, fallback ->
                    Text(
                        text = "${idx + 2}. $fallback",
                        style = MaterialTheme.typography.bodySmall,
                        color = OmniTextSecondary
                    )
                    if (idx < rule.fallbackProviders.size - 1) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = OmniTextMuted,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
