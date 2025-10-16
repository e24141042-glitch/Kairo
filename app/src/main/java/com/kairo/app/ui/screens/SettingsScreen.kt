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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kairo.app.data.UserPreferences
import com.kairo.app.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val userPreferences by viewModel.userPreferences.collectAsState(initial = UserPreferences())
    
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
        }
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
                            // TODO: Show confirmation dialog
                            viewModel.deleteCompletedTasks()
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
                            // TODO: Show confirmation dialog
                            viewModel.deleteAllTasks()
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
                            text = "Version 1.0.0",
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
