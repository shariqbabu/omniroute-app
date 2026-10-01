package com.omniroute.app.ui.screens.providers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.omniroute.app.data.model.AiProvider
import com.omniroute.app.data.model.ProviderCategory
import com.omniroute.app.data.model.ProviderStatus
import com.omniroute.app.ui.theme.*
import com.omniroute.app.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvidersScreen(
    viewModel: MainViewModel
) {
    val providers by viewModel.providers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ProviderCategory?>(null) }
    var editingProvider by remember { mutableStateOf<AiProvider?>(null) }

    val filteredProviders = remember(providers, searchQuery, selectedCategory) {
        providers.filter { provider ->
            val matchesCategory = selectedCategory == null || provider.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    provider.name.contains(searchQuery, ignoreCase = true) ||
                    provider.availableModels.any { it.contains(searchQuery, ignoreCase = true) }
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI Providers Hub",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OmniTextPrimary
                        )
                        Text(
                            text = "${providers.count { it.isEnabled }} active of 359 total providers",
                            style = MaterialTheme.typography.bodySmall,
                            color = OmniSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshAll() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = OmniTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OmniBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OmniBackground)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search providers & models...", color = OmniTextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OmniTextSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = OmniTextSecondary)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = OmniSurface,
                    unfocusedContainerColor = OmniSurface,
                    focusedBorderColor = OmniPrimary,
                    unfocusedBorderColor = OmniCardBorder,
                    focusedTextColor = OmniTextPrimary,
                    unfocusedTextColor = OmniTextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All (359)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OmniPrimary,
                            selectedLabelColor = OmniTextPrimary,
                            containerColor = OmniCard,
                            labelColor = OmniTextSecondary
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == ProviderCategory.FREE_TIER,
                        onClick = { selectedCategory = ProviderCategory.FREE_TIER },
                        label = { Text("150+ Free Tiers") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OmniSecondary,
                            selectedLabelColor = OmniBackground,
                            containerColor = OmniCard,
                            labelColor = OmniTextSecondary
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == ProviderCategory.TIER_ONE,
                        onClick = { selectedCategory = ProviderCategory.TIER_ONE },
                        label = { Text("Tier 1 (Claude/GPT/Gemini)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OmniPrimary,
                            selectedLabelColor = OmniTextPrimary,
                            containerColor = OmniCard,
                            labelColor = OmniTextSecondary
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == ProviderCategory.CLOUD_AGGREGATOR,
                        onClick = { selectedCategory = ProviderCategory.CLOUD_AGGREGATOR },
                        label = { Text("Cloud LPUs (Groq/OpenRouter)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OmniAccentCyan,
                            selectedLabelColor = OmniBackground,
                            containerColor = OmniCard,
                            labelColor = OmniTextSecondary
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == ProviderCategory.CHINESE_MODELS,
                        onClick = { selectedCategory = ProviderCategory.CHINESE_MODELS },
                        label = { Text("DeepSeek / Kimi / Qwen") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OmniAccentPurple,
                            selectedLabelColor = OmniTextPrimary,
                            containerColor = OmniCard,
                            labelColor = OmniTextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Providers List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProviders, key = { it.id }) { provider ->
                    ProviderCardItem(
                        provider = provider,
                        onToggle = { viewModel.toggleProvider(provider.id) },
                        onEdit = { editingProvider = provider }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    if (editingProvider != null) {
        val p = editingProvider!!
        var apiKeyInput by remember { mutableStateOf(p.apiKey) }
        var baseUrlInput by remember { mutableStateOf(p.baseUrl) }

        AlertDialog(
            onDismissRequest = { editingProvider = null },
            title = { Text("Configure ${p.name}", color = OmniTextPrimary) },
            containerColor = OmniSurface,
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Category: ${p.category.name.replace("_", " ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = OmniSecondary
                    )
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("API Key (Overrides Master Key)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = OmniTextPrimary,
                            unfocusedTextColor = OmniTextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = baseUrlInput,
                        onValueChange = { baseUrlInput = it },
                        label = { Text("Custom Base URL (Optional)") },
                        placeholder = { Text("e.g. http://localhost:11434") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = OmniTextPrimary,
                            unfocusedTextColor = OmniTextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(onClick = { editingProvider = null }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingProvider = null }) {
                    Text("Cancel", color = OmniTextSecondary)
                }
            }
        )
    }
}

@Composable
fun ProviderCardItem(
    provider: AiProvider,
    onToggle: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = OmniCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, OmniCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = provider.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OmniTextPrimary
                    )
                    if (provider.isFreeTier) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "FREE",
                            color = OmniSecondary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Priority #${provider.priority} • ${provider.latencyMs}ms",
                        style = MaterialTheme.typography.bodySmall,
                        color = OmniTextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${provider.availableModels.size} models",
                        style = MaterialTheme.typography.bodySmall,
                        color = OmniTextMuted
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = OmniTextSecondary)
                }
                Switch(
                    checked = provider.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = OmniPrimary,
                        uncheckedThumbColor = OmniTextMuted,
                        uncheckedTrackColor = OmniSurface
                    )
                )
            }
        }
    }
}
