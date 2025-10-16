package com.kairo.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairo.app.data.Task
import com.kairo.app.di.AppContainer
import com.kairo.app.data.UserPreferences
import com.kairo.app.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
 

/**
 * ViewModel for adding and editing tasks
 */
class AddEditTaskViewModel(
    private val taskRepository: TaskRepository = AppContainer.taskRepository
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(AddEditTaskUiState())
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()
    
    /**
     * Load task for editing
     */
    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val task = taskRepository.getTaskById(taskId)
                _uiState.value = _uiState.value.copy(
                    task = task,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load task: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Update task title
     */
    fun updateTitle(title: String) {
        val currentTask = _uiState.value.task
        _uiState.value = _uiState.value.copy(
            task = currentTask?.copy(title = title) ?: Task(
                title = title,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )
    }
    
    /**
     * Update task description
     */
    fun updateDescription(description: String) {
        val currentTask = _uiState.value.task
        _uiState.value = _uiState.value.copy(
            task = currentTask?.copy(description = description) ?: Task(
                title = "",
                description = description,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )
    }
    
    /**
     * Update task priority
     */
    fun updatePriority(priority: Task.Priority) {
        val currentTask = _uiState.value.task
        _uiState.value = _uiState.value.copy(
            task = currentTask?.copy(priority = priority) ?: Task(
                title = "",
                priority = priority,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )
    }
    
    /**
     * Update task category
     */
    fun updateCategory(category: String) {
        val currentTask = _uiState.value.task
        _uiState.value = _uiState.value.copy(
            task = currentTask?.copy(category = category) ?: Task(
                title = "",
                category = category,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )
    }
    
    /**
     * Update task due date
     */
    fun updateDueDate(dueDate: LocalDateTime?) {
        val currentTask = _uiState.value.task
        _uiState.value = _uiState.value.copy(
            task = currentTask?.copy(dueDate = dueDate) ?: Task(
                title = "",
                dueDate = dueDate,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )
    }
    
    /**
     * Update task reminder time
     */
    fun updateReminderTime(reminderTime: LocalDateTime?) {
        val currentTask = _uiState.value.task
        _uiState.value = _uiState.value.copy(
            task = currentTask?.copy(reminderTime = reminderTime) ?: Task(
                title = "",
                reminderTime = reminderTime,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )
    }
    
    /**
     * Save the task
     */
    fun saveTask() {
        val task = _uiState.value.task
        if (task == null || task.title.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Task title cannot be empty"
            )
            return
        }
        
        viewModelScope.launch {
            try {
                if (task.id == 0L) {
                    // New task
                    taskRepository.insertTask(task)
                } else {
                    // Update existing task
                    taskRepository.updateTask(task)
                }
                _uiState.value = _uiState.value.copy(
                    isSaved = true,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to save task: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Reset save state
     */
    fun resetSaveState() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}

/**
 * UI State for Add/Edit Task screen
 */
data class AddEditTaskUiState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)
