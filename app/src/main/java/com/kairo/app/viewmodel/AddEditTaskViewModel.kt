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
import com.kairo.app.data.RepeatInterval
import java.util.Date
import com.kairo.app.data.Priority
 

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
                    isLoading = false,
                    dueDateTime = task?.dueDateTime,
                    repeatInterval = task?.repeatInterval ?: RepeatInterval.NONE,
                    repeatEndDate = task?.repeatEndDate
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
                description = "",
                dueDateTime = null,
                priority = Priority.LOW,
                isCompleted = false,
                createdAt = Date(),
                completedAt = null,
                isSynced = false,
                googleCalendarEventId = null,
                repeatInterval = RepeatInterval.NONE,
                repeatEndDate = null
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
                dueDateTime = null,
                priority = Priority.LOW,
                isCompleted = false,
                createdAt = Date(),
                completedAt = null,
                isSynced = false,
                googleCalendarEventId = null,
                repeatInterval = RepeatInterval.NONE,
                repeatEndDate = null
            )
        )
    }
    
    /**
     * Update task priority
     */
    fun updatePriority(priority: Priority) {
        val currentTask = _uiState.value.task
        _uiState.value = _uiState.value.copy(
            task = currentTask?.copy(priority = priority) ?: Task(
                title = "",
                description = "",
                dueDateTime = null,
                priority = priority,
                isCompleted = false,
                createdAt = Date(),
                completedAt = null,
                isSynced = false,
                googleCalendarEventId = null,
                repeatInterval = RepeatInterval.NONE,
                repeatEndDate = null
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
                description = "",
                dueDateTime = null,
                priority = Priority.LOW,
                isCompleted = false,
                createdAt = Date(),
                completedAt = null,
                isSynced = false,
                googleCalendarEventId = null,
                repeatInterval = RepeatInterval.NONE,
                repeatEndDate = null
            )
        )
    }
    
    /**
     * Update task due date and time
     */
    fun updateDueDateTime(dueDateTime: Date?) {
        val currentTask = _uiState.value.task
        _uiState.value = _uiState.value.copy(
            task = currentTask?.copy(dueDateTime = dueDateTime) ?: Task(
                title = "",
                description = "",
                dueDateTime = dueDateTime,
                priority = Priority.LOW,
                isCompleted = false,
                createdAt = Date(),
                completedAt = null,
                isSynced = false,
                googleCalendarEventId = null,
                repeatInterval = RepeatInterval.NONE,
                repeatEndDate = null
            ),
            dueDateTime = dueDateTime
        )
    }

    /**
     * Update task repeat interval
     */
    fun updateRepeatInterval(repeatInterval: RepeatInterval) {
        val currentTask = _uiState.value.task
        _uiState.value = _uiState.value.copy(
            task = currentTask?.copy(repeatInterval = repeatInterval) ?: Task(
                title = "",
                description = "",
                dueDateTime = null,
                priority = Priority.LOW,
                isCompleted = false,
                createdAt = Date(),
                completedAt = null,
                isSynced = false,
                googleCalendarEventId = null,
                repeatInterval = repeatInterval,
                repeatEndDate = null
            ),
            repeatInterval = repeatInterval
        )
    }

    /**
     * Update task repeat end date
     */
    fun updateRepeatEndDate(repeatEndDate: Date?) {
        val currentTask = _uiState.value.task
        _uiState.value = _uiState.value.copy(
            task = currentTask?.copy(repeatEndDate = repeatEndDate) ?: Task(
                title = "",
                description = "",
                dueDateTime = null,
                priority = Priority.LOW,
                isCompleted = false,
                createdAt = Date(),
                completedAt = null,
                isSynced = false,
                googleCalendarEventId = null,
                repeatInterval = RepeatInterval.NONE,
                repeatEndDate = repeatEndDate
            ),
            repeatEndDate = repeatEndDate
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
                val taskToSave = task.copy(
                    dueDateTime = _uiState.value.dueDateTime,
                    repeatInterval = _uiState.value.repeatInterval,
                    repeatEndDate = _uiState.value.repeatEndDate
                )
                if (taskToSave.id == 0L) {
                    // New task
                    taskRepository.insertTask(taskToSave)
                } else {
                    // Update existing task
                    taskRepository.updateTask(taskToSave)
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
    val errorMessage: String? = null,
    val dueDateTime: Date? = null,
    val repeatInterval: RepeatInterval = RepeatInterval.NONE,
    val repeatEndDate: Date? = null
)
