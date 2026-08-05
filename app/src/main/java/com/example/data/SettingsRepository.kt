package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemePreference {
    SYSTEM, LIGHT, DARK
}

class SettingsRepository(private val context: Context) {
    
    companion object {
        val THEME_PREF_KEY = stringPreferencesKey("theme_preference")
    }

    val themePreference: Flow<ThemePreference> = context.dataStore.data
        .map { preferences ->
            val themeName = preferences[THEME_PREF_KEY] ?: ThemePreference.SYSTEM.name
            try {
                ThemePreference.valueOf(themeName)
            } catch (e: Exception) {
                ThemePreference.SYSTEM
            }
        }

    suspend fun saveThemePreference(theme: ThemePreference) {
        context.dataStore.edit { preferences ->
            preferences[THEME_PREF_KEY] = theme.name
        }
    }
}
