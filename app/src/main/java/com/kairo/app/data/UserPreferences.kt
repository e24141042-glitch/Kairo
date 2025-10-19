package com.kairo.app.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Data class representing user preferences
 */
data class UserPreferences(
    val isDarkTheme: Boolean = false,
    val accentColor: String = "blue",
    val showCompletedTasks: Boolean = true,
    val sortOrder: SortOrder = SortOrder.DATE_CREATED,
    val defaultCategory: String = "General",
    val enableNotifications: Boolean = true,
    val reminderTime: Long = 3600000L, // 1 hour in milliseconds
    val dailyQuoteEnabled: Boolean = true,
    val googleCalendarAccount: String? = null
) {
    enum class SortOrder {
        DATE_CREATED,
        DATE_DUE,
        PRIORITY,
        TITLE,
        CATEGORY
    }
}

/**
 * Repository for managing user preferences using DataStore
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {

    private object PreferencesKeys {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val SHOW_COMPLETED_TASKS = booleanPreferencesKey("show_completed_tasks")
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val DEFAULT_CATEGORY = stringPreferencesKey("default_category")
        val ENABLE_NOTIFICATIONS = booleanPreferencesKey("enable_notifications")
        val REMINDER_TIME = longPreferencesKey("reminder_time")
        val DAILY_QUOTE_ENABLED = booleanPreferencesKey("daily_quote_enabled")
        val GOOGLE_CALENDAR_ACCOUNT = stringPreferencesKey("google_calendar_account")
    }

    val userPreferences: Flow<UserPreferences> = dataStore.data
        .map { preferences ->
            UserPreferences(
                isDarkTheme = preferences[PreferencesKeys.IS_DARK_THEME] ?: false,
                accentColor = preferences[PreferencesKeys.ACCENT_COLOR] ?: "blue",
                showCompletedTasks = preferences[PreferencesKeys.SHOW_COMPLETED_TASKS] ?: true,
                sortOrder = try {
                    UserPreferences.SortOrder.valueOf(
                        preferences[PreferencesKeys.SORT_ORDER] ?: "DATE_CREATED"
                    )
                } catch (e: IllegalArgumentException) {
                    UserPreferences.SortOrder.DATE_CREATED
                },
                defaultCategory = preferences[PreferencesKeys.DEFAULT_CATEGORY] ?: "General",
                enableNotifications = preferences[PreferencesKeys.ENABLE_NOTIFICATIONS] ?: true,
                reminderTime = preferences[PreferencesKeys.REMINDER_TIME] ?: 3600000L,
                dailyQuoteEnabled = preferences[PreferencesKeys.DAILY_QUOTE_ENABLED] ?: true,
                googleCalendarAccount = preferences[PreferencesKeys.GOOGLE_CALENDAR_ACCOUNT]
            )
        }

    suspend fun updateDarkTheme(isDarkTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] = isDarkTheme
        }
    }

    suspend fun updateAccentColor(color: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCENT_COLOR] = color
        }
    }

    suspend fun updateShowCompletedTasks(showCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_COMPLETED_TASKS] = showCompleted
        }
    }

    suspend fun updateSortOrder(sortOrder: UserPreferences.SortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateDefaultCategory(category: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_CATEGORY] = category
        }
    }

    suspend fun updateEnableNotifications(enable: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ENABLE_NOTIFICATIONS] = enable
        }
    }

    suspend fun updateReminderTime(timeMs: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_TIME] = timeMs
        }
    }

    suspend fun updateDailyQuoteEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_QUOTE_ENABLED] = enabled
        }
    }

    suspend fun updateGoogleCalendarAccount(account: String?) {
        dataStore.edit { preferences ->
            if (account != null) {
                preferences[PreferencesKeys.GOOGLE_CALENDAR_ACCOUNT] = account
            } else {
                preferences.remove(PreferencesKeys.GOOGLE_CALENDAR_ACCOUNT)
            }
        }
    }
}
