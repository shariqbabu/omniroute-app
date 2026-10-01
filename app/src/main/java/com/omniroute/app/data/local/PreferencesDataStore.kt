package com.omniroute.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.omniroute.app.data.model.AppSettings
import com.omniroute.app.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "omniroute_preferences")

class PreferencesDataStore(private val context: Context) {

    companion object {
        val SERVER_URL = stringPreferencesKey("server_url")
        val MASTER_API_KEY = stringPreferencesKey("master_api_key")
        val ACTIVE_MODEL = stringPreferencesKey("active_model")
        val RTK_COMPRESSION = booleanPreferencesKey("rtk_compression")
        val CAVEMAN_MODE = booleanPreferencesKey("caveman_mode")
        val AUTO_FALLBACK = booleanPreferencesKey("auto_fallback")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val TEMPERATURE = floatPreferencesKey("temperature")
        val SYSTEM_PROMPT = stringPreferencesKey("system_prompt")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            serverUrl = prefs[SERVER_URL] ?: "http://10.0.2.2:3000",
            masterApiKey = prefs[MASTER_API_KEY] ?: "",
            activeModel = prefs[ACTIVE_MODEL] ?: "claude-3-5-sonnet",
            rtkCompressionEnabled = prefs[RTK_COMPRESSION] ?: true,
            cavemanMode = prefs[CAVEMAN_MODE] ?: true,
            autoFallbackEnabled = prefs[AUTO_FALLBACK] ?: true,
            themeMode = try {
                ThemeMode.valueOf(prefs[THEME_MODE] ?: ThemeMode.AMOLED_DARK.name)
            } catch (e: Exception) {
                ThemeMode.AMOLED_DARK
            },
            temperature = prefs[TEMPERATURE] ?: 0.7f,
            systemPrompt = prefs[SYSTEM_PROMPT] ?: "You are an intelligent AI assistant powered by OmniRoute Gateway."
        )
    }

    suspend fun saveServerUrl(url: String) {
        context.dataStore.edit { prefs ->
            prefs[SERVER_URL] = url.trimEnd('/')
        }
    }

    suspend fun saveMasterApiKey(key: String) {
        context.dataStore.edit { prefs ->
            prefs[MASTER_API_KEY] = key
        }
    }

    suspend fun saveActiveModel(model: String) {
        context.dataStore.edit { prefs ->
            prefs[ACTIVE_MODEL] = model
        }
    }

    suspend fun setRtkCompression(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[RTK_COMPRESSION] = enabled
        }
    }

    suspend fun setCavemanMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[CAVEMAN_MODE] = enabled
        }
    }

    suspend fun setAutoFallback(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[AUTO_FALLBACK] = enabled
        }
    }

    suspend fun setTemperature(temp: Float) {
        context.dataStore.edit { prefs ->
            prefs[TEMPERATURE] = temp
        }
    }

    suspend fun setSystemPrompt(prompt: String) {
        context.dataStore.edit { prefs ->
            prefs[SYSTEM_PROMPT] = prompt
        }
    }
}
