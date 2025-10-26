package com.kairo.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairo.app.data.UserPreferences
import com.kairo.app.di.AppContainer
import com.kairo.app.data.UserPreferencesRepository
import com.kairo.app.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.ExistingWorkPolicy
import com.kairo.app.data.google_calendar.GoogleCalendarSyncWorker
import android.content.Context
 
/**
 * ViewModel for managing settings and user preferences
 */
class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository = AppContainer.userPreferencesRepository,
    private val taskRepository: TaskRepository = AppContainer.taskRepository,
    private val context: Context = AppContainer.context
) : ViewModel() {
    
    // User preferences state
    val userPreferences: Flow<UserPreferences> = userPreferencesRepository.userPreferences

    /**
     * Update theme preference (alias for existing UI call)
     */
    fun updateDarkTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateDarkTheme(isDarkTheme)
        }
    }

    /**
     * Also expose a semantic setter
     */
    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateDarkTheme(enabled)
        }
    }

    /**
     * Update notification preference
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateEnableNotifications(enabled)
        }
    }

    /**
     * Update show completed tasks preference
     */
    fun updateShowCompletedTasks(showCompleted: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateShowCompletedTasks(showCompleted)
        }
    }

    /**
     * Update daily quote enabled preference
     */
    fun updateDailyQuoteEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateDailyQuoteEnabled(enabled)
        }
    }

    /**
     * Update sort order preference (match UI signature)
     */
    fun updateSortOrder(sortOrder: UserPreferences.SortOrder) {
        viewModelScope.launch {
            userPreferencesRepository.updateSortOrder(sortOrder)
        }
    }

    /**
     * Update default category
     */
    fun updateDefaultCategory(category: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateDefaultCategory(category)
        }
    }

    /**
     * Clear completed tasks
     */
    fun deleteCompletedTasks() {
        viewModelScope.launch {
            taskRepository.deleteCompletedTasks()
        }
    }

    /**
     * Clear all tasks
     */
    fun deleteAllTasks() {
        viewModelScope.launch {
            try {
                // Delete linked Google Calendar events for all tasks before clearing
                val allTasks = taskRepository.getAllTasks().first()
                allTasks.forEach { t ->
                    t.googleCalendarEventId?.let { eventId ->
                        try {
                            AppContainer.googleCalendarService.deleteEventFromGoogleCalendar(eventId)
                        } catch (e: Exception) {
                            android.util.Log.w("SettingsViewModel", "Failed to delete Google Calendar event $eventId: ${e.message}")
                        }
                    }
                }
                taskRepository.deleteAllTasks()
            } catch (e: Exception) {
                android.util.Log.e("SettingsViewModel", "Failed to delete all tasks: ${e.message}", e)
            }
        }
    }

    /**
     * Connect Google Calendar account
     */
    fun connectGoogleCalendar(email: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateGoogleCalendarAccount(email)
            // Ensure calendar exists and ID is persisted
            AppContainer.googleCalendarService.createKairoCalendar()
            // Initial PUSH: populate Google Calendar with existing local tasks
            val tasks = taskRepository.getAllTasks().first()
            AppContainer.googleCalendarService.syncTasksToGoogleCalendar(tasks)
        }
    }

    /**
     * Disconnect Google Calendar account
     */
    fun disconnectGoogleCalendar() {
        viewModelScope.launch {
            userPreferencesRepository.updateGoogleCalendarAccount(null)
            userPreferencesRepository.updateGoogleCalendarId(null)
            // Reset sync status when disconnecting
            userPreferencesRepository.updateSyncStatus(UserPreferences.SyncStatus.IDLE)
            userPreferencesRepository.updateLastSyncTime(null)
            userPreferencesRepository.updateSyncErrorMessage(null)
        }
    }

    /**
     * Trigger manual Google Calendar sync
     */
    fun triggerManualSync() {
        viewModelScope.launch {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncWorkRequest = OneTimeWorkRequestBuilder<GoogleCalendarSyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "GoogleCalendarSync",
                ExistingWorkPolicy.REPLACE,
                syncWorkRequest
            )
        }
    }
}
