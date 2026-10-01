package com.omniroute.app.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.omniroute.app.ui.theme.*
import com.omniroute.app.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()

    var serverUrl by remember(settings.serverUrl) { mutableStateOf(settings.serverUrl) }
    var masterKey by remember(settings.masterApiKey) { mutableStateOf(settings.masterApiKey) }
    var systemPrompt by remember(settings.systemPrompt) { mutableStateOf(settings.systemPrompt) }
    var temp by remember(settings.temperature) { mutableStateOf(settings.temperature) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gateway Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OmniTextPrimary
                    )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = OmniSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OmniCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "VPS / Remote Server Connection",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OmniTextPrimary
                        )

                        OutlinedTextField(
                            value = serverUrl,
                            onValueChange = { serverUrl = it },
                            label = { Text("VPS Server Base URL") },
                            placeholder = { Text("http://1.2.3.4:3000") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = OmniTextPrimary,
                                unfocusedTextColor = OmniTextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = masterKey,
                            onValueChange = { masterKey = it },
                            label = { Text("Master API Secret (Optional)") },
                            placeholder = { Text("Bearer sk-omniroute-...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = OmniTextPrimary,
                                unfocusedTextColor = OmniTextPrimary
                            )
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = OmniSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OmniCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Inference & System Prompt Defaults",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OmniTextPrimary
                        )

                        OutlinedTextField(
                            value = systemPrompt,
                            onValueChange = { systemPrompt = it },
                            label = { Text("Default System Prompt") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = OmniTextPrimary,
                                unfocusedTextColor = OmniTextPrimary
                            )
                        )

                        Text(
                            text = "Temperature: ${String.format("%.2f", temp)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OmniTextSecondary
                        )

                        Slider(
                            value = temp,
                            onValueChange = { temp = it },
                            valueRange = 0.0f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = OmniPrimary,
                                activeTrackColor = OmniPrimary
                            )
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        viewModel.updateServerSettings(
                            url = serverUrl,
                            apiKey = masterKey,
                            rtk = settings.rtkCompressionEnabled,
                            caveman = settings.cavemanMode,
                            autoFallback = settings.autoFallbackEnabled,
                            temp = temp,
                            systemPrompt = systemPrompt
                        )
                        Toast.makeText(context, "Settings saved successfully", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OmniPrimary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Apply Configuration", fontWeight = FontWeight.Bold)
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = OmniCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("OmniRoute Android Mobile", style = MaterialTheme.typography.titleSmall, color = OmniTextPrimary)
                        Text("Release v3.8.51 • MIT License", style = MaterialTheme.typography.bodySmall, color = OmniTextMuted)
                        Text("One Endpoint, 359 Providers, 1200+ Models", style = MaterialTheme.typography.labelSmall, color = OmniSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
