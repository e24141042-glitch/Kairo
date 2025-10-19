package com.kairo.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairo.app.data.Task
import com.kairo.app.data.UserPreferences
import com.kairo.app.repository.TaskRepository
import com.kairo.app.repository.TaskStatistics
import com.kairo.app.di.AppContainer
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.kairo.app.data.Priority
 

/**
 * ViewModel for managing tasks and UI state
 */
class TaskViewModel(
    private val taskRepository: TaskRepository = AppContainer.taskRepository
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    // Task statistics
    private val _statistics = MutableStateFlow(TaskStatistics(0, 0, 0, 0))
    val statistics: StateFlow<TaskStatistics> = _statistics.asStateFlow()
    
    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Filter state
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()
    
    init {
        loadTasks()
        loadStatistics()
    }
    
    /**
     * Load all tasks
     */
    private fun loadTasks() {
        viewModelScope.launch {
            combine(
                taskRepository.getAllTasks(),
                searchQuery,
                filterState
            ) { tasks, query, filter ->
                var filteredTasks = tasks
                
                // Apply search filter
                if (query.isNotBlank()) {
                    filteredTasks = filteredTasks.filter { task ->
                        task.title.contains(query, ignoreCase = true) ||
                        task.description.contains(query, ignoreCase = true)
                    }
                }
                
                // Apply category filter
                if (filter.selectedCategory.isNotBlank() && filter.selectedCategory != "All") {
                    filteredTasks = filteredTasks.filter { it.category == filter.selectedCategory }
                }
                
                // Apply priority filter
                if (filter.selectedPriority != null) {
                    filteredTasks = filteredTasks.filter { it.priority == filter.selectedPriority }
                }
                
                // Apply completion filter
                when (filter.completionFilter) {
                    CompletionFilter.ALL -> { /* No filter */ }
                    CompletionFilter.COMPLETED -> filteredTasks = filteredTasks.filter { it.isCompleted }
                    CompletionFilter.PENDING -> filteredTasks = filteredTasks.filter { !it.isCompleted }
                    CompletionFilter.OVERDUE -> filteredTasks = filteredTasks.filter { it.isOverdue }
                }
                
                filteredTasks
            }.collect { tasks ->
                _uiState.value = _uiState.value.copy(tasks = tasks)
            }
        }
    }
    
    /**
     * Load task statistics
     */
    private fun loadStatistics() {
        viewModelScope.launch {
            _statistics.value = taskRepository.getTaskStatistics()
        }
    }
    
    /**
     * Add a new task
     */
    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.insertTask(task)
                loadStatistics() // Refresh statistics
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to add task: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Update an existing task
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.updateTask(task)
                loadStatistics() // Refresh statistics
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update task: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Delete a task
     */
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(task)
                loadStatistics() // Refresh statistics
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to delete task: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Toggle task completion status
     */
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.toggleTaskCompletion(task)
                loadStatistics() // Refresh statistics
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update task: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Update search query
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * Update filter state
     */
    fun updateFilterState(filter: FilterState) {
        _filterState.value = filter
    }
    
    /**
     * Clear error message
     */
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Delete all completed tasks
     */
    fun deleteCompletedTasks() {
        viewModelScope.launch {
            try {
                taskRepository.deleteCompletedTasks()
                loadStatistics() // Refresh statistics
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to delete completed tasks: ${e.message}"
                )
            }
        }
    }
}

/**
 * UI State for Task screen
 */
data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Filter state for tasks
 */
data class FilterState(
    val selectedCategory: String = "All",
    val selectedPriority: Priority? = null,
    val completionFilter: CompletionFilter = CompletionFilter.ALL
)

/**
 * Completion filter options
 */
enum class CompletionFilter {
    ALL,
    COMPLETED,
    PENDING,
    OVERDUE
}
