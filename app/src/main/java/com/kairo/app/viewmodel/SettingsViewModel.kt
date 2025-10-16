package com.kairo.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairo.app.data.UserPreferences
import com.kairo.app.di.AppContainer
import com.kairo.app.data.UserPreferencesRepository
import com.kairo.app.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
 

/**
 * ViewModel for managing settings and user preferences
 */
class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository = AppContainer.userPreferencesRepository,
    private val taskRepository: TaskRepository = AppContainer.taskRepository
) : ViewModel() {
    
    // User preferences state
    val userPreferences: Flow<UserPreferences> = userPreferencesRepository.userPreferences
    
    /**
     * Update dark theme preference
     */
    fun updateDarkTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateDarkTheme(isDarkTheme)
        }
    }
    
    /**
     * Update accent color preference
     */
    fun updateAccentColor(color: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateAccentColor(color)
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
     * Update sort order preference
     */
    fun updateSortOrder(sortOrder: UserPreferences.SortOrder) {
        viewModelScope.launch {
            userPreferencesRepository.updateSortOrder(sortOrder)
        }
    }
    
    /**
     * Update default category preference
     */
    fun updateDefaultCategory(category: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateDefaultCategory(category)
        }
    }
    
    /**
     * Update enable notifications preference
     */
    fun updateEnableNotifications(enable: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateEnableNotifications(enable)
        }
    }
    
    /**
     * Update reminder time preference
     */
    fun updateReminderTime(timeMs: Long) {
        viewModelScope.launch {
            userPreferencesRepository.updateReminderTime(timeMs)
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
     * Delete all tasks
     */
    fun deleteAllTasks() {
        viewModelScope.launch {
            taskRepository.deleteAllTasks()
        }
    }
    
    /**
     * Delete completed tasks
     */
    fun deleteCompletedTasks() {
        viewModelScope.launch {
            taskRepository.deleteCompletedTasks()
        }
    }
}
