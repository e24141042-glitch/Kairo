package com.example.quicktasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicktasks.model.Task
import com.example.quicktasks.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Task Details screen
 * Manages individual task details and actions
 */
@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TaskDetailsUiState())
    val uiState: StateFlow<TaskDetailsUiState> = _uiState.asStateFlow()
    
    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val task = taskRepository.getTaskById(taskId)
            if (task != null) {
                _uiState.value = _uiState.value.copy(
                    task = task,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Task not found",
                    isLoading = false
                )
            }
        }
    }
    
    fun toggleTaskCompletion() {
        val currentTask = _uiState.value.task
        if (currentTask != null) {
            viewModelScope.launch {
                taskRepository.updateTaskCompletion(currentTask.id, !currentTask.completedStatus)
                // Reload task to get updated state
                loadTask(currentTask.id)
            }
        }
    }
    
    fun deleteTask() {
        val currentTask = _uiState.value.task
        if (currentTask != null) {
            viewModelScope.launch {
                taskRepository.deleteTask(currentTask)
                _uiState.value = _uiState.value.copy(taskDeleted = true)
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun resetDeletedState() {
        _uiState.value = _uiState.value.copy(taskDeleted = false)
    }
}

/**
 * UI state for the Task Details screen
 */
data class TaskDetailsUiState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val taskDeleted: Boolean = false
)

