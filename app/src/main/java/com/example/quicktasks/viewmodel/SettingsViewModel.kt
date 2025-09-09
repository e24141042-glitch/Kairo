package com.example.quicktasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicktasks.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Settings screen
 * Manages app settings and preferences
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsRepository.isDarkThemeEnabled(),
                settingsRepository.areNotificationsEnabled(),
                settingsRepository.isCloudBackupEnabled(),
                settingsRepository.getNotificationTime()
            ) { isDarkTheme: Boolean, notificationsEnabled: Boolean, cloudBackup: Boolean, notificationTime: Pair<Int, Int> ->
                SettingsUiState(
                    isDarkTheme = isDarkTheme,
                    notificationsEnabled = notificationsEnabled,
                    cloudBackupEnabled = cloudBackup,
                    notificationTime = notificationTime,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
    
    fun toggleDarkTheme() {
        viewModelScope.launch {
            val currentState = _uiState.value
            settingsRepository.setDarkTheme(!currentState.isDarkTheme)
        }
    }
    
    fun toggleNotifications() {
        viewModelScope.launch {
            val currentState = _uiState.value
            settingsRepository.setNotificationsEnabled(!currentState.notificationsEnabled)
        }
    }
    
    fun toggleCloudBackup() {
        viewModelScope.launch {
            val currentState = _uiState.value
            settingsRepository.setCloudBackupEnabled(!currentState.cloudBackupEnabled)
        }
    }
    
    fun updateNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            settingsRepository.setNotificationTime(hour, minute)
        }
    }
    
    fun clearAllData() {
        viewModelScope.launch {
            try {
                settingsRepository.clearAllData()
                _uiState.value = _uiState.value.copy(
                    showClearDataDialog = false,
                    dataCleared = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to clear data: ${e.message}"
                )
            }
        }
    }
    
    fun showClearDataDialog() {
        _uiState.value = _uiState.value.copy(showClearDataDialog = true)
    }
    
    fun hideClearDataDialog() {
        _uiState.value = _uiState.value.copy(showClearDataDialog = false)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun resetDataClearedState() {
        _uiState.value = _uiState.value.copy(dataCleared = false)
    }
}

/**
 * UI state for the Settings screen
 */
data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val cloudBackupEnabled: Boolean = false,
    val notificationTime: Pair<Int, Int> = Pair(9, 0), // 9:00 AM
    val isLoading: Boolean = true,
    val showClearDataDialog: Boolean = false,
    val dataCleared: Boolean = false,
    val errorMessage: String? = null
)
