package com.example.quicktasks.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quicktasks.viewmodel.SettingsViewModel

/**
 * Settings screen for app configuration and preferences
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Section
            SettingsSection(
                title = "Appearance",
                icon = Icons.Default.Palette
            ) {
                SettingsItem(
                    title = "Dark Theme",
                    subtitle = "Use dark theme for the app",
                    icon = Icons.Default.DarkMode,
                    trailing = {
                        Switch(
                            checked = uiState.isDarkTheme,
                            onCheckedChange = { viewModel.toggleDarkTheme() }
                        )
                    }
                )
            }
            
            // Notifications Section
            SettingsSection(
                title = "Notifications",
                icon = Icons.Default.Notifications
            ) {
                SettingsItem(
                    title = "Enable Notifications",
                    subtitle = "Receive reminders for your tasks",
                    icon = Icons.Default.NotificationsActive,
                    trailing = {
                        Switch(
                            checked = uiState.notificationsEnabled,
                            onCheckedChange = { viewModel.toggleNotifications() }
                        )
                    }
                )
                
                if (uiState.notificationsEnabled) {
                    SettingsItem(
                        title = "Notification Time",
                        subtitle = "Daily reminder time",
                        icon = Icons.Default.Schedule,
                        onClick = {
                            // Time picker would be implemented here
                        }
                    )
                }
            }
            
            // Data Section
            SettingsSection(
                title = "Data & Storage",
                icon = Icons.Default.Storage
            ) {
                SettingsItem(
                    title = "Cloud Backup",
                    subtitle = "Sync your tasks to the cloud",
                    icon = Icons.Default.CloudSync,
                    trailing = {
                        Switch(
                            checked = uiState.cloudBackupEnabled,
                            onCheckedChange = { viewModel.toggleCloudBackup() }
                        )
                    }
                )
                
                SettingsItem(
                    title = "Clear All Data",
                    subtitle = "Delete all tasks and settings",
                    icon = Icons.Default.DeleteForever,
                    onClick = { viewModel.showClearDataDialog() },
                    textColor = MaterialTheme.colorScheme.error
                )
            }
            
            // About Section
            SettingsSection(
                title = "About",
                icon = Icons.Default.Info
            ) {
                SettingsItem(
                    title = "App Version",
                    subtitle = "1.0.0",
                    icon = Icons.Default.Info
                )
                
                SettingsItem(
                    title = "Privacy Policy",
                    subtitle = "View our privacy policy",
                    icon = Icons.Default.PrivacyTip,
                    onClick = { /* Navigate to privacy policy */ }
                )
                
                SettingsItem(
                    title = "Terms of Service",
                    subtitle = "View terms of service",
                    icon = Icons.Default.Description,
                    onClick = { /* Navigate to terms */ }
                )
            }
        }
    }
    
    // Clear data confirmation dialog
    if (uiState.showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideClearDataDialog() },
            title = { Text("Clear All Data") },
            text = { 
                Text("This will permanently delete all your tasks and settings. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.clearAllData() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Clear All Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideClearDataDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Data cleared confirmation
    if (uiState.dataCleared) {
        LaunchedEffect(Unit) {
            // Show snackbar or navigate back
            viewModel.resetDataClearedState()
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            content()
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor.copy(alpha = 0.7f)
                )
            }
        }
        
        trailing?.invoke()
    }
}
