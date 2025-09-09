package com.example.quicktasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicktasks.model.Task
import com.example.quicktasks.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for the Home screen
 * Manages today's tasks, upcoming tasks, and completed tasks
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    init {
        loadTasks()
        observeSearchQuery()
    }
    
    private fun loadTasks() {
        viewModelScope.launch {
            combine(
                taskRepository.getActiveTasks(),
                taskRepository.getCompletedTasks(),
                _searchQuery
            ) { activeTasks, completedTasks, query ->
                val now = LocalDateTime.now()
                val today = LocalDate.now()
                val startOfDay = today.atStartOfDay()
                val endOfDay = today.plusDays(1).atStartOfDay()
                
                val filteredActiveTasks = if (query.isBlank()) {
                    activeTasks
                } else {
                    activeTasks.filter { task ->
                        task.title.contains(query, ignoreCase = true) ||
                        task.description.contains(query, ignoreCase = true) ||
                        task.category.contains(query, ignoreCase = true)
                    }
                }
                
                val todaysTasks = filteredActiveTasks.filter { task ->
                    task.dueDate?.toLocalDate() == today
                }
                
                val upcomingTasks = filteredActiveTasks.filter { task ->
                    task.dueDate?.isAfter(now) == true && task.dueDate?.toLocalDate() != today
                }.sortedBy { it.dueDate }
                
                val overdueTasks = filteredActiveTasks.filter { task ->
                    task.dueDate?.isBefore(now) == true && !task.completedStatus
                }
                
                val recentCompletedTasks = completedTasks.take(10)
                
                HomeUiState(
                    todaysTasks = todaysTasks,
                    upcomingTasks = upcomingTasks,
                    overdueTasks = overdueTasks,
                    recentCompletedTasks = recentCompletedTasks,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
    
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery.collect {
                loadTasks()
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTaskCompletion(task.id, !task.completedStatus)
        }
    }
    
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
    }
}

/**
 * UI state for the Home screen
 */
data class HomeUiState(
    val todaysTasks: List<Task> = emptyList(),
    val upcomingTasks: List<Task> = emptyList(),
    val overdueTasks: List<Task> = emptyList(),
    val recentCompletedTasks: List<Task> = emptyList(),
    val isLoading: Boolean = true
)

