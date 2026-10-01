package com.omniroute.app.ui.screens.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.omniroute.app.data.model.RequestLog
import com.omniroute.app.ui.theme.*
import com.omniroute.app.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    viewModel: MainViewModel
) {
    val logs by viewModel.logs.collectAsState()
    var selectedLog by remember { mutableStateOf<RequestLog?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Live Audit Logs",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OmniTextPrimary
                        )
                        Text(
                            text = "Real-time routing & token telemetry",
                            style = MaterialTheme.typography.bodySmall,
                            color = OmniSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshAll() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = OmniTextSecondary)
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }
            items(logs, key = { it.id }) { log ->
                LogItemCard(log = log, onClick = { selectedLog = log })
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (selectedLog != null) {
        val log = selectedLog!!
        AlertDialog(
            onDismissRequest = { selectedLog = null },
            title = { Text("Request Inspection", color = OmniTextPrimary) },
            containerColor = OmniSurface,
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Model: ${log.requestedModel}", color = OmniTextPrimary, fontWeight = FontWeight.Bold)
                    Text("Resolved Provider: ${log.resolvedProvider}", color = OmniSecondary)
                    Text("Latency: ${log.latencyMs}ms", color = OmniTextSecondary)
                    Text("Tokens: Prompt ${log.promptTokens} | Completion ${log.completionTokens}", color = OmniTextSecondary)
                    Text("Token Savings: -${log.tokensSavedPercent}%", color = OmniSecondary, fontWeight = FontWeight.Bold)
                    if (log.fallbackOccurred) {
                        Text("Fallback Chain: ${log.fallbackChain.joinToString(" ➔ ")}", color = OmniWarning)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedLog = null }) {
                    Text("Close", color = OmniPrimary)
                }
            }
        )
    }
}

@Composable
fun LogItemCard(
    log: RequestLog,
    onClick: () -> Unit
) {
    val dateStr = remember(log.timestamp) {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = OmniCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, OmniCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${log.statusCode} OK",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (log.statusCode == 200) OmniSuccess else OmniError,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = log.requestedModel,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = OmniTextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${log.resolvedProvider} • ${log.latencyMs}ms",
                        style = MaterialTheme.typography.bodySmall,
                        color = OmniTextSecondary
                    )
                    if (log.fallbackOccurred) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.AltRoute,
                            contentDescription = "Fallback",
                            tint = OmniWarning,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "-${log.tokensSavedPercent}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = OmniSecondary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = OmniTextMuted
                )
            }
        }
    }
}
