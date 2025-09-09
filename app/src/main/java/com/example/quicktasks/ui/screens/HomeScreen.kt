package com.example.quicktasks.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quicktasks.model.Task
import com.example.quicktasks.model.TaskPriority
import com.example.quicktasks.ui.components.TaskCard
import com.example.quicktasks.ui.components.TaskPriorityIndicator
import com.example.quicktasks.viewmodel.HomeViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Home screen displaying today's tasks, upcoming tasks, and completed tasks
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddTask: () -> Unit,
    onNavigateToTaskDetails: (Long) -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QuickTasks") },
                actions = {
                    IconButton(onClick = onNavigateToCategories) {
                        Icon(Icons.Default.Category, contentDescription = "Categories")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    println("HomeScreen: FAB clicked, navigating to AddTask")
                    onNavigateToAddTask()
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onClearClick = viewModel::clearSearch,
                modifier = Modifier.padding(16.dp)
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Overdue Tasks Section
                if (uiState.overdueTasks.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Overdue Tasks",
                            count = uiState.overdueTasks.size,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    items(uiState.overdueTasks) { task ->
                        TaskCard(
                            task = task,
                            onClick = { onNavigateToTaskDetails(task.id) },
                            onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                            onDelete = { viewModel.deleteTask(task) },
                            isOverdue = true
                        )
                    }
                }
                
                // Today's Tasks Section
                item {
                    SectionHeader(
                        title = "Today's Tasks",
                        count = uiState.todaysTasks.size,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                items(uiState.todaysTasks) { task ->
                    TaskCard(
                        task = task,
                        onClick = { onNavigateToTaskDetails(task.id) },
                        onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
                
                // Upcoming Tasks Section
                if (uiState.upcomingTasks.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Upcoming Tasks",
                            count = uiState.upcomingTasks.size,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    items(uiState.upcomingTasks) { task ->
                        TaskCard(
                            task = task,
                            onClick = { onNavigateToTaskDetails(task.id) },
                            onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
                
                // Recent Completed Tasks Section
                if (uiState.recentCompletedTasks.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Recently Completed",
                            count = uiState.recentCompletedTasks.size,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    items(uiState.recentCompletedTasks) { task ->
                        TaskCard(
                            task = task,
                            onClick = { onNavigateToTaskDetails(task.id) },
                            onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                            onDelete = { viewModel.deleteTask(task) },
                            isCompleted = true
                        )
                    }
                }
                
                // Empty State
                if (uiState.todaysTasks.isEmpty() && 
                    uiState.upcomingTasks.isEmpty() && 
                    uiState.overdueTasks.isEmpty() && 
                    uiState.recentCompletedTasks.isEmpty()) {
                    item {
                        EmptyState(
                            title = if (searchQuery.isBlank()) "No tasks yet" else "No tasks found",
                            subtitle = if (searchQuery.isBlank()) "Tap + to add your first task" else "Try adjusting your search",
                            icon = if (searchQuery.isBlank()) Icons.Default.Add else Icons.Default.Search
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search tasks...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Surface(
            color = color.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun EmptyState(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}
