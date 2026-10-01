package com.omniroute.app.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omniroute.app.data.model.GatewayStats
import com.omniroute.app.ui.theme.*
import com.omniroute.app.ui.viewmodel.MainViewModel

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToPlayground: () -> Unit,
    onNavigateToProviders: () -> Unit,
    onNavigateToRouting: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val models by viewModel.models.collectAsState()

    var showModelPicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(OmniBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HeaderStatusCard(
                stats = stats,
                serverUrl = settings.serverUrl,
                onRefresh = { viewModel.refreshAll() },
                onSettingsClick = onNavigateToSettings
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Tokens Saved",
                    value = "${stats.averageTokenSavingsPercent}%",
                    subtitle = "4.1M Tokens Saved",
                    icon = Icons.Default.ElectricBolt,
                    accentColor = OmniSecondary
                )
                KpiMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Active Providers",
                    value = "${stats.activeProvidersCount}/359",
                    subtitle = "150+ Free Tiers",
                    icon = Icons.Default.Hub,
                    accentColor = OmniAccentPurple
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Requests",
                    value = "${stats.totalRequests}",
                    subtitle = "Avg ${stats.avgLatencyMs}ms Latency",
                    icon = Icons.Default.TrendingUp,
                    accentColor = OmniAccentCyan
                )
                KpiMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Cost Saved",
                    value = "$${stats.estimatedCostSavedUsd}",
                    subtitle = "via Smart Fallbacks",
                    icon = Icons.Default.Savings,
                    accentColor = OmniWarning
                )
            }
        }

        item {
            ActiveModelSelectorCard(
                activeModel = settings.activeModel,
                onClick = { showModelPicker = true },
                onLaunchChat = onNavigateToPlayground
            )
        }

        item {
            TokenCompressionStatusCard(
                rtkEnabled = settings.rtkCompressionEnabled,
                cavemanEnabled = settings.cavemanMode,
                onManageRouting = onNavigateToRouting
            )
        }

        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                color = OmniTextPrimary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = "Open Playground",
                    icon = Icons.Default.ChatBubble,
                    onClick = onNavigateToPlayground
                )
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = "Manage 359 Providers",
                    icon = Icons.Default.SettingsSuggest,
                    onClick = onNavigateToProviders
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showModelPicker) {
        AlertDialog(
            onDismissRequest = { showModelPicker = false },
            title = { Text("Select Active Model", color = OmniTextPrimary) },
            containerColor = OmniSurface,
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(models.size) { index ->
                        val model = models[index]
                        val isSelected = model.id == settings.activeModel
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectModel(model.id)
                                    showModelPicker = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) OmniPrimary.copy(alpha = 0.2f) else OmniCard
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, OmniPrimary) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = model.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = OmniTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${model.provider} • ${if (model.isFree) "Free Tier" else "Pay-as-you-go"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OmniTextSecondary
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = OmniPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showModelPicker = false }) {
                    Text("Close", color = OmniPrimary)
                }
            }
        )
    }
}

@Composable
fun HeaderStatusCard(
    stats: GatewayStats,
    serverUrl: String,
    onRefresh: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = OmniSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, OmniCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (stats.isOnline) OmniSuccess else OmniError)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (stats.isOnline) "Gateway Live" else "Gateway Offline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OmniTextPrimary
                    )
                }
                Row {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = OmniTextSecondary)
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = OmniTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Target: ${serverUrl.ifEmpty { "Not Configured" }}",
                style = MaterialTheme.typography.labelSmall,
                color = OmniTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Engine: ${stats.serverVersion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OmniTextMuted
                )
                Text(
                    text = "Uptime: ${stats.uptimeHours}h (99.8%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = OmniSuccess
                )
            }
        }
    }
}

@Composable
fun KpiMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
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
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = OmniTextSecondary
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = OmniTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = OmniTextMuted
            )
        }
    }
}

@Composable
fun ActiveModelSelectorCard(
    activeModel: String,
    onClick: () -> Unit,
    onLaunchChat: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = OmniSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, OmniPrimary.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ACTIVE MODEL ROUTE",
                    style = MaterialTheme.typography.labelSmall,
                    color = OmniPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = activeModel,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OmniTextPrimary
                )
                Text(
                    text = "Tap to switch between 1200+ models",
                    style = MaterialTheme.typography.bodySmall,
                    color = OmniTextSecondary
                )
            }
            IconButton(
                onClick = onLaunchChat,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(OmniPrimary)
            ) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Open Chat",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun TokenCompressionStatusCard(
    rtkEnabled: Boolean,
    cavemanEnabled: Boolean,
    onManageRouting: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = OmniCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, OmniCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RTK + Caveman Compression",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OmniTextPrimary
                    )
                    Text(
                        text = "Real-time Token Saver (15%–95% Reduction)",
                        style = MaterialTheme.typography.bodySmall,
                        color = OmniSecondary
                    )
                }
                TextButton(onClick = onManageRouting) {
                    Text("Configure", color = OmniPrimary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = {},
                    label = { Text("RTK: ${if (rtkEnabled) "Active" else "Off"}") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (rtkEnabled) OmniSecondary.copy(alpha = 0.2f) else OmniSurface,
                        labelColor = if (rtkEnabled) OmniSecondary else OmniTextMuted
                    )
                )
                AssistChip(
                    onClick = {},
                    label = { Text("Caveman: ${if (cavemanEnabled) "Active" else "Off"}") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (cavemanEnabled) OmniAccentCyan.copy(alpha = 0.2f) else OmniSurface,
                        labelColor = if (cavemanEnabled) OmniAccentCyan else OmniTextMuted
                    )
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = OmniCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, OmniCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OmniPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = OmniTextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
