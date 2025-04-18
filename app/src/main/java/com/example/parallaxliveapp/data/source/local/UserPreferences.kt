package com.example.parallaxliveapp.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    companion object {
        private val LAST_LOGGED_IN_USER_ID = stringPreferencesKey("last_logged_in_user_id")
        private val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val APP_THEME = stringPreferencesKey("app_theme")
    }

    val lastLoggedInUserId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_LOGGED_IN_USER_ID]
        }

    val isDarkModeEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_ENABLED] ?: false
        }

    val areNotificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: true
        }

    val appTheme: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[APP_THEME] ?: "system"
        }

    suspend fun saveLastLoggedInUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_LOGGED_IN_USER_ID] = userId
        }
    }

    suspend fun clearLastLoggedInUserId() {
        context.dataStore.edit { preferences ->
            preferences.remove(LAST_LOGGED_IN_USER_ID)
        }
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_ENABLED] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setAppTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[APP_THEME] = theme
        }
    }
}