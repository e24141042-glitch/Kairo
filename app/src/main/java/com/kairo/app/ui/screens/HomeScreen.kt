package com.kairo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kairo.app.ui.components.*
import com.kairo.app.viewmodel.TaskViewModel
import com.kairo.app.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddTask: () -> Unit,
    onNavigateToEditTask: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    taskViewModel: TaskViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val uiState by taskViewModel.uiState.collectAsState()
    val statistics by taskViewModel.statistics.collectAsState()
    val searchQuery by taskViewModel.searchQuery.collectAsState()
    val filterState by taskViewModel.filterState.collectAsState()
    val userPreferences by settingsViewModel.userPreferences.collectAsState(initial = com.kairo.app.data.UserPreferences())
    
    val listState = rememberLazyListState()
    
    LaunchedEffect(Unit) {
        // Load categories for filtering
        // This would typically come from the repository
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Kairo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        )
        
        // Content
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progress Overview
            item {
                ProgressOverview(statistics = statistics)
            }
            
            // Daily Quote
            if (userPreferences.dailyQuoteEnabled) {
                item {
                    DailyQuoteCard()
                }
            }
            
            // Search and Filter
            item {
                SearchAndFilterBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = taskViewModel::updateSearchQuery,
                    filterState = filterState,
                    onFilterStateChange = taskViewModel::updateFilterState,
                    availableCategories = emptyList() // TODO: Get from repository
                )
            }
            
            // Tasks Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tasks",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (statistics.completedTasks > 0) {
                        TextButton(
                            onClick = { taskViewModel.deleteCompletedTasks() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete completed",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear Completed")
                        }
                    }
                }
            }
            
            // Tasks List
            if (uiState.tasks.isEmpty()) {
                item {
                    EmptyTasksState(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                items(
                    items = uiState.tasks,
                    key = { it.id }
                ) { task ->
                    TaskCard(
                        task = task,
                        onTaskClick = { onNavigateToEditTask(task.id) },
                        onToggleCompletion = taskViewModel::toggleTaskCompletion,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
    
    // Floating Action Button
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = onNavigateToAddTask,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Task"
            )
        }
    }
}

@Composable
private fun EmptyTasksState(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📝",
                style = MaterialTheme.typography.displayLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No tasks yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Tap the + button to create your first task",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
