package com.example.quicktasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicktasks.model.Task
import com.example.quicktasks.model.TaskPriority
import com.example.quicktasks.model.RecurringType
import com.example.quicktasks.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * ViewModel for the Add/Edit Task screen
 * Manages task creation and editing state
 */
@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()
    
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()
    
    private val _taskSaved = MutableStateFlow(false)
    val taskSaved: StateFlow<Boolean> = _taskSaved.asStateFlow()
    
    init {
        loadCategories()
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            taskRepository.getAllCategories().collect { categories ->
                _uiState.value = _uiState.value.copy(availableCategories = categories)
            }
        }
    }
    
    fun loadTaskForEditing(taskId: Long) {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId)
            task?.let {
                _uiState.value = _uiState.value.copy(
                    title = it.title,
                    description = it.description,
                    dueDate = it.dueDate,
                    priority = it.priority,
                    category = it.category,
                    reminderTime = it.reminderTime,
                    isRecurring = it.isRecurring,
                    recurringType = it.recurringType
                )
                _isEditing.value = true
            }
        }
    }
    
    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }
    
    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }
    
    fun updateDueDate(dueDate: LocalDateTime?) {
        _uiState.value = _uiState.value.copy(dueDate = dueDate)
    }
    
    fun updatePriority(priority: TaskPriority) {
        _uiState.value = _uiState.value.copy(priority = priority)
    }
    
    fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }
    
    fun updateReminderTime(reminderTime: LocalDateTime?) {
        _uiState.value = _uiState.value.copy(reminderTime = reminderTime)
    }
    
    fun updateRecurring(isRecurring: Boolean) {
        _uiState.value = _uiState.value.copy(isRecurring = isRecurring)
    }
    
    fun updateRecurringType(recurringType: RecurringType?) {
        _uiState.value = _uiState.value.copy(recurringType = recurringType)
    }
    
    fun saveTask() {
        val currentState = _uiState.value
        if (currentState.title.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Title cannot be empty")
            return
        }
        
        viewModelScope.launch {
            try {
                val task = Task(
                    id = if (_isEditing.value) currentState.taskId else 0,
                    title = currentState.title.trim(),
                    description = currentState.description.trim(),
                    dueDate = currentState.dueDate,
                    priority = currentState.priority,
                    category = currentState.category.ifBlank { "General" },
                    reminderTime = currentState.reminderTime,
                    isRecurring = currentState.isRecurring,
                    recurringType = currentState.recurringType
                )
                
                if (_isEditing.value) {
                    taskRepository.updateTask(task)
                } else {
                    taskRepository.insertTask(task)
                }
                
                _uiState.value = AddTaskUiState() // Reset form
                _isEditing.value = false
                _taskSaved.value = true
            } catch (e: Exception) {
                _uiState.value = currentState.copy(errorMessage = "Failed to save task: ${e.message}")
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun resetForm() {
        _uiState.value = AddTaskUiState()
        _isEditing.value = false
        _taskSaved.value = false
    }
    
    fun resetTaskSaved() {
        _taskSaved.value = false
    }
}

/**
 * UI state for the Add/Edit Task screen
 */
data class AddTaskUiState(
    val taskId: Long = 0,
    val title: String = "",
    val description: String = "",
    val dueDate: LocalDateTime? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val category: String = "",
    val reminderTime: LocalDateTime? = null,
    val isRecurring: Boolean = false,
    val recurringType: RecurringType? = null,
    val availableCategories: List<String> = emptyList(),
    val errorMessage: String? = null
)
