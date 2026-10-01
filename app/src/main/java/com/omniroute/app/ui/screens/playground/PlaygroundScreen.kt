package com.omniroute.app.ui.screens.playground

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omniroute.app.data.model.ChatMessage
import com.omniroute.app.data.model.MessageRole
import com.omniroute.app.ui.theme.*
import com.omniroute.app.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val messages by viewModel.chatMessages.collectAsState()
    val isStreaming by viewModel.isStreaming.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val models by viewModel.models.collectAsState()

    var inputPrompt by remember { mutableStateOf("") }
    var showModelMenu by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size, messages.lastOrNull()?.content?.length) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    ) {
                        Column {
                            Text(
                                text = "OmniRoute Playground",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OmniTextPrimary
                            )
                            FilterChip(
                                selected = true,
                                onClick = { showModelMenu = true },
                                label = {
                                    Text(
                                        text = settings.activeModel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OmniPrimary
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = OmniPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = OmniSurface
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = OmniPrimary.copy(alpha = 0.5f),
                                    enabled = true,
                                    selected = true
                                )
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Clear Session",
                            tint = OmniTextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OmniBackground)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(OmniSurface)
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(8.dp)
            ) {
                if (messages.isEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        val samplePrompts = listOf(
                            "⚡ Benchmark token compression",
                            "🐍 Write a Python FastAPI endpoint",
                            "🔄 Test fallback chain logic",
                            "🤖 Explain DeepSeek-R1 reasoning"
                        )
                        items(samplePrompts) { prompt ->
                            SuggestionChip(
                                onClick = {
                                    inputPrompt = prompt
                                    viewModel.sendChatMessage(prompt)
                                },
                                label = { Text(prompt, style = MaterialTheme.typography.bodySmall) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = OmniCard,
                                    labelColor = OmniTextSecondary
                                )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputPrompt,
                        onValueChange = { inputPrompt = it },
                        placeholder = {
                            Text(
                                "Ask via ${settings.activeModel}...",
                                color = OmniTextMuted
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = OmniCard,
                            unfocusedContainerColor = OmniCard,
                            focusedTextColor = OmniTextPrimary,
                            unfocusedTextColor = OmniTextPrimary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputPrompt.isNotBlank() && !isStreaming) {
                                val text = inputPrompt
                                inputPrompt = ""
                                viewModel.sendChatMessage(text)
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isStreaming) OmniWarning else OmniPrimary)
                    ) {
                        if (isStreaming) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OmniBackground)
                .padding(padding)
        ) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Bolt,
                        contentDescription = null,
                        tint = OmniPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Unified AI Gateway Playground",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = OmniTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "All 359 providers & 1200+ models available.\nRTK & Caveman compression active.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OmniTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        PlaygroundMessageBubble(
                            message = msg,
                            onCopy = { text ->
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("OmniRoute Response", text)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showModelMenu) {
        AlertDialog(
            onDismissRequest = { showModelMenu = false },
            title = { Text("Switch Model Route", color = OmniTextPrimary) },
            containerColor = OmniSurface,
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(models) { model ->
                        val isSelected = model.id == settings.activeModel
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp)),
                            onClick = {
                                viewModel.selectModel(model.id)
                                showModelMenu = false
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) OmniPrimary.copy(alpha = 0.2f) else OmniCard
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = model.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = OmniTextPrimary
                                    )
                                    if (model.isFree) {
                                        Text("FREE", color = OmniSecondary, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                Text(
                                    text = "${model.provider} • Context: ${model.contextWindow / 1000}k tokens",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OmniTextSecondary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showModelMenu = false }) {
                    Text("Done", color = OmniPrimary)
                }
            }
        )
    }
}

@Composable
fun PlaygroundMessageBubble(
    message: ChatMessage,
    onCopy: (String) -> Unit
) {
    val isUser = message.role == MessageRole.USER
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isUser) OmniPrimaryDark else OmniCard

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isUser) "You" else (message.modelName ?: "OmniRoute AI"),
                style = MaterialTheme.typography.labelSmall,
                color = if (isUser) OmniPrimary else OmniSecondary,
                fontWeight = FontWeight.Bold
            )
            if (!isUser && message.latencyMs != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• ${message.latencyMs}ms",
                    style = MaterialTheme.typography.labelSmall,
                    color = OmniTextMuted
                )
            }
            if (!isUser && message.tokenSavingsPercent != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• -${message.tokenSavingsPercent}% tokens",
                    style = MaterialTheme.typography.labelSmall,
                    color = OmniSecondary
                )
            }
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, OmniCardBorder) else null,
            modifier = Modifier.widthIn(max = 340.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                SelectionContainerText(
                    text = message.content.ifEmpty { "Thinking & routing across providers..." }
                )

                if (!isUser && message.content.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { onCopy(message.content) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = OmniTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionContainerText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = OmniTextPrimary,
            lineHeight = 20.sp
        )
    )
}
