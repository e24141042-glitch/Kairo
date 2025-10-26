package com.kairo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kairo.app.data.UserPreferences
import com.kairo.app.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onSignInGoogleCalendar: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val userPreferences by viewModel.userPreferences.collectAsState(initial = UserPreferences())
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Confirmation dialog states
    var showDeleteAllTasksDialog by remember { mutableStateOf(false) }
    var showDeleteCompletedTasksDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Appearance Section
            SettingsSection(title = "Appearance") {
                // Dark Theme Toggle
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = "Dark Theme",
                    subtitle = "Use dark theme"
                ) {
                    Switch(
                        checked = userPreferences.isDarkTheme,
                        onCheckedChange = viewModel::updateDarkTheme
                    )
                }

                // Show Completed Tasks Toggle
                SettingsItem(
                    icon = Icons.Default.Visibility,
                    title = "Show Completed Tasks",
                    subtitle = "Display completed tasks in the list"
                ) {
                    Switch(
                        checked = userPreferences.showCompletedTasks,
                        onCheckedChange = viewModel::updateShowCompletedTasks
                    )
                }

                // Daily Quote Toggle
                SettingsItem(
                    icon = Icons.Default.FormatQuote,
                    title = "Daily Quote",
                    subtitle = "Show motivational quotes on home screen"
                ) {
                    Switch(
                        checked = userPreferences.dailyQuoteEnabled,
                        onCheckedChange = viewModel::updateDailyQuoteEnabled
                    )
                }
            }

            // Task Management Section
            SettingsSection(title = "Task Management") {
                // Sort Order
                SortOrderSelector(
                    currentSortOrder = userPreferences.sortOrder,
                    onSortOrderChange = viewModel::updateSortOrder
                )

                // Default Category
                OutlinedTextField(
                    value = userPreferences.defaultCategory,
                    onValueChange = viewModel::updateDefaultCategory,
                    label = { Text("Default Category") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Integrations Section
            SettingsSection(title = "Integrations") {
                val connectedAccount = userPreferences.googleCalendarAccount
                val coroutineScope = rememberCoroutineScope()

                SettingsItem(
                    icon = Icons.Default.CalendarMonth,
                    title = "Google Calendar Sync",
                    subtitle = if (connectedAccount != null) "Connected as $connectedAccount" else "Not connected"
                ) {
                    if (connectedAccount != null) {
                        Button(onClick = {
                            viewModel.disconnectGoogleCalendar()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Disconnected from Google Calendar")
                            }
                        }) {
                            Text("Disconnect")
                        }
                    } else {
                        Button(onClick = onSignInGoogleCalendar) {
                            Text("Connect")
                        }
                    }
                }

                // Sync Status Display (only show if connected)
                if (connectedAccount != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    SyncStatusDisplay(
                        syncStatus = userPreferences.syncStatus,
                        lastSyncTime = userPreferences.lastSyncTime,
                        errorMessage = userPreferences.syncErrorMessage,
                        onManualSync = { viewModel.triggerManualSync() }
                    )
                }
            }

            // Data Management Section
            SettingsSection(title = "Data Management") {
                // Delete Completed Tasks
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Delete Completed Tasks",
                    subtitle = "Remove all completed tasks"
                ) {
                    TextButton(
                        onClick = {
                            showDeleteCompletedTasksDialog = true
                        }
                    ) {
                        Text("Clear")
                    }
                }

                // Delete All Tasks
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Delete All Tasks",
                    subtitle = "Remove all tasks (this cannot be undone)"
                ) {
                    TextButton(
                        onClick = {
                            showDeleteAllTasksDialog = true
                        }
                    ) {
                        Text("Clear All")
                    }
                }
            }

            // About Section
            SettingsSection(title = "About") {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Kairo",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "v2.2",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "A simple and elegant to-do app built with Jetpack Compose and Material 3.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
    
    // Confirmation dialog for deleting completed tasks
    if (showDeleteCompletedTasksDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteCompletedTasksDialog = false },
            title = { Text("Delete Completed Tasks") },
            text = { Text("Are you sure you want to delete all completed tasks? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCompletedTasks()
                        showDeleteCompletedTasksDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteCompletedTasksDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Confirmation dialog for deleting all tasks
    if (showDeleteAllTasksDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllTasksDialog = false },
            title = { Text("Delete All Tasks") },
            text = { Text("Are you sure you want to delete ALL tasks? This will permanently remove all your tasks and cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllTasks()
                        showDeleteAllTasksDialog = false
                    }
                ) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAllTasksDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        content()
    }
}

@Composable
private fun SyncStatusDisplay(
    syncStatus: UserPreferences.SyncStatus,
    lastSyncTime: Long?,
    errorMessage: String?,
    onManualSync: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (syncStatus) {
                UserPreferences.SyncStatus.SUCCESS -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                UserPreferences.SyncStatus.ERROR -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                UserPreferences.SyncStatus.SYNCING -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                UserPreferences.SyncStatus.IDLE -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (icon, iconColor, statusText) = when (syncStatus) {
                        UserPreferences.SyncStatus.SUCCESS -> Triple(
                            Icons.Default.CheckCircle,
                            MaterialTheme.colorScheme.primary,
                            "Sync successful"
                        )
                        UserPreferences.SyncStatus.ERROR -> Triple(
                            Icons.Default.Error,
                            MaterialTheme.colorScheme.error,
                            "Sync failed"
                        )
                        UserPreferences.SyncStatus.SYNCING -> Triple(
                            Icons.Default.Sync,
                            MaterialTheme.colorScheme.secondary,
                            "Syncing..."
                        )
                        UserPreferences.SyncStatus.IDLE -> Triple(
                            Icons.Default.Schedule,
                            MaterialTheme.colorScheme.onSurfaceVariant,
                            "Ready to sync"
                        )
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = statusText,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        if (lastSyncTime != null) {
                            val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                            Text(
                                text = "Last sync: ${formatter.format(Date(lastSyncTime))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (syncStatus != UserPreferences.SyncStatus.SYNCING) {
                    TextButton(onClick = onManualSync) {
                        Text("Sync Now")
                    }
                }
            }

            if (syncStatus == UserPreferences.SyncStatus.ERROR && !errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Error: $errorMessage",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SortOrderSelector(
    currentSortOrder: UserPreferences.SortOrder,
    onSortOrderChange: (UserPreferences.SortOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = "Sort Order",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Sort Order",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = getSortOrderDescription(currentSortOrder),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = getSortOrderDisplayName(currentSortOrder),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .width(120.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    UserPreferences.SortOrder.values().forEach { sortOrder ->
                        DropdownMenuItem(
                            text = { Text(getSortOrderDisplayName(sortOrder)) },
                            onClick = {
                                onSortOrderChange(sortOrder)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun getSortOrderDisplayName(sortOrder: UserPreferences.SortOrder): String {
    return when (sortOrder) {
        UserPreferences.SortOrder.DATE_CREATED -> "Date Created"
        UserPreferences.SortOrder.DATE_DUE -> "Due Date"
        UserPreferences.SortOrder.PRIORITY -> "Priority"
        UserPreferences.SortOrder.TITLE -> "Title"
        UserPreferences.SortOrder.CATEGORY -> "Category"
    }
}

private fun getSortOrderDescription(sortOrder: UserPreferences.SortOrder): String {
    return when (sortOrder) {
        UserPreferences.SortOrder.DATE_CREATED -> "Newest tasks first"
        UserPreferences.SortOrder.DATE_DUE -> "Due soon first"
        UserPreferences.SortOrder.PRIORITY -> "High priority first"
        UserPreferences.SortOrder.TITLE -> "Alphabetical order"
        UserPreferences.SortOrder.CATEGORY -> "Group by category"
    }
}
