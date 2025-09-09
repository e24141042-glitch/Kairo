package com.example.quicktasks.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing app settings and preferences
 * Uses DataStore for persistent storage of user preferences
 */
@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    
    private val darkThemeKey = booleanPreferencesKey("dark_theme")
    private val notificationsEnabledKey = booleanPreferencesKey("notifications_enabled")
    private val cloudBackupEnabledKey = booleanPreferencesKey("cloud_backup_enabled")
    private val notificationHourKey = intPreferencesKey("notification_hour")
    private val notificationMinuteKey = intPreferencesKey("notification_minute")
    
    fun isDarkThemeEnabled(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[darkThemeKey] ?: false
    }
    
    fun areNotificationsEnabled(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[notificationsEnabledKey] ?: true
    }
    
    fun isCloudBackupEnabled(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[cloudBackupEnabledKey] ?: false
    }
    
    fun getNotificationTime(): Flow<Pair<Int, Int>> = context.dataStore.data.map { preferences ->
        val hour = preferences[notificationHourKey] ?: 9
        val minute = preferences[notificationMinuteKey] ?: 0
        Pair(hour, minute)
    }
    
    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[darkThemeKey] = enabled
        }
    }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[notificationsEnabledKey] = enabled
        }
    }
    
    suspend fun setCloudBackupEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[cloudBackupEnabledKey] = enabled
        }
    }
    
    suspend fun setNotificationTime(hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[notificationHourKey] = hour
            preferences[notificationMinuteKey] = minute
        }
    }
    
    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

