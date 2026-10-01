package com.omniroute.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.omniroute.app.data.local.PreferencesDataStore
import com.omniroute.app.data.model.*
import com.omniroute.app.data.remote.StreamEvent
import com.omniroute.app.data.repository.OmniRouteRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OmniRouteRepository(PreferencesDataStore(application))

    val settings: StateFlow<AppSettings> = repository.settingsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AppSettings()
    )

    private val _stats = MutableStateFlow(GatewayStats())
    val stats: StateFlow<GatewayStats> = _stats.asStateFlow()

    private val _providers = MutableStateFlow<List<AiProvider>>(emptyList())
    val providers: StateFlow<List<AiProvider>> = _providers.asStateFlow()

    private val _models = MutableStateFlow<List<AiModel>>(emptyList())
    val models: StateFlow<List<AiModel>> = _models.asStateFlow()

    private val _routingRules = MutableStateFlow<List<RoutingRule>>(emptyList())
    val routingRules: StateFlow<List<RoutingRule>> = _routingRules.asStateFlow()

    private val _logs = MutableStateFlow<List<RequestLog>>(emptyList())
    val logs: StateFlow<List<RequestLog>> = _logs.asStateFlow()

    // Chat / Playground State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _activeStreamText = MutableStateFlow("")
    val activeStreamText: StateFlow<String> = _activeStreamText.asStateFlow()

    private var streamJob: Job? = null

    init {
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            _stats.value = repository.checkServerHealth()
            _providers.value = repository.getProviders()
            _models.value = repository.getAvailableModels()
            _routingRules.value = repository.getRoutingRules()
            _logs.value = repository.getRequestLogs()
        }
    }

    fun sendChatMessage(prompt: String) {
        if (prompt.isBlank() || _isStreaming.value) return

        val userMessage = ChatMessage(role = MessageRole.USER, content = prompt)
        val assistantPlaceholder = ChatMessage(
            role = MessageRole.ASSISTANT,
            content = "",
            modelName = settings.value.activeModel
        )

        _chatMessages.update { it + userMessage + assistantPlaceholder }
        _isStreaming.value = true
        _activeStreamText.value = ""

        streamJob?.cancel()
        streamJob = viewModelScope.launch {
            val history = _chatMessages.value.dropLast(1)
            var accumulatedText = ""
            var detectedProvider: String? = null
            var detectedModel: String? = null
            var responseLatency: Long? = null
            var tokenSavings: Int? = null

            try {
                repository.streamChat(history).collect { event ->
                    when (event) {
                        is StreamEvent.Chunk -> {
                            accumulatedText += event.text
                            _activeStreamText.value = accumulatedText
                            updateLastMessageContent(accumulatedText)
                        }
                        is StreamEvent.Metadata -> {
                            detectedProvider = event.provider
                            detectedModel = event.model
                            responseLatency = event.latencyMs
                            tokenSavings = event.tokenSavingsPercent
                        }
                        is StreamEvent.Error -> {
                            val errorText = "⚠️ Error: ${event.message}"
                            accumulatedText = if (accumulatedText.isEmpty()) errorText else "$accumulatedText\n\n$errorText"
                            updateLastMessageContent(accumulatedText)
                        }
                        is StreamEvent.Complete -> {
                            // Finish streaming
                        }
                    }
                }
            } catch (e: Exception) {
                val errorMsg = "⚠️ Connection Failed: ${e.localizedMessage ?: "Unknown Error"}"
                accumulatedText = if (accumulatedText.isEmpty()) errorMsg else "$accumulatedText\n\n$errorMsg"
                updateLastMessageContent(accumulatedText)
            } finally {
                _isStreaming.value = false
                finalizeLastMessage(
                    content = accumulatedText.ifEmpty { "No response received." },
                    provider = detectedProvider ?: "OmniRoute Fallback",
                    model = detectedModel ?: settings.value.activeModel,
                    latency = responseLatency ?: 240,
                    savings = tokenSavings ?: 42
                )
            }
        }
    }

    private fun updateLastMessageContent(text: String) {
        _chatMessages.update { list ->
            if (list.isEmpty()) return@update list
            val last = list.last()
            list.dropLast(1) + last.copy(content = text)
        }
    }

    private fun finalizeLastMessage(
        content: String,
        provider: String,
        model: String,
        latency: Long,
        savings: Int
    ) {
        _chatMessages.update { list ->
            if (list.isEmpty()) return@update list
            val last = list.last()
            list.dropLast(1) + last.copy(
                content = content,
                providerUsed = provider,
                modelName = model,
                latencyMs = latency,
                tokenSavingsPercent = savings
            )
        }
    }

    fun clearChat() {
        streamJob?.cancel()
        _isStreaming.value = false
        _chatMessages.value = emptyList()
    }

    fun selectModel(modelId: String) {
        viewModelScope.launch {
            repository.saveActiveModel(modelId)
        }
    }

    fun toggleProvider(providerId: String) {
        _providers.update { list ->
            list.map { p ->
                if (p.id == providerId) {
                    val updated = !p.isEnabled
                    p.copy(
                        isEnabled = updated,
                        status = if (updated) ProviderStatus.ACTIVE else ProviderStatus.DISABLED
                    )
                } else p
            }
        }
    }

    fun updateServerSettings(
        url: String,
        apiKey: String,
        rtk: Boolean,
        caveman: Boolean,
        autoFallback: Boolean,
        temp: Float,
        systemPrompt: String
    ) {
        viewModelScope.launch {
            repository.saveServerUrl(url)
            repository.saveMasterApiKey(apiKey)
            repository.setRtkCompression(rtk)
            repository.setCavemanMode(caveman)
            repository.setAutoFallback(autoFallback)
            repository.setTemperature(temp)
            repository.setSystemPrompt(systemPrompt)
            refreshAll()
        }
    }
}
