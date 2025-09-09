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
 * ViewModel for the Categories screen
 * Manages task filtering by categories and category management
 */
@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    init {
        loadCategories()
        observeCategorySelection()
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            taskRepository.getAllCategories().collect { categories ->
                _uiState.value = _uiState.value.copy(
                    categories = categories,
                    isLoading = false
                )
            }
        }
    }
    
    private fun observeCategorySelection() {
        viewModelScope.launch {
            _selectedCategory.collect { category ->
                if (category != null) {
                    taskRepository.getTasksByCategory(category).collect { tasks ->
                        _uiState.value = _uiState.value.copy(
                            tasksInCategory = tasks,
                            selectedCategory = category
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        tasksInCategory = emptyList(),
                        selectedCategory = null
                    )
                }
            }
        }
    }
    
    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }
    
    fun clearCategorySelection() {
        _selectedCategory.value = null
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
}

/**
 * UI state for the Categories screen
 */
data class CategoriesUiState(
    val categories: List<String> = emptyList(),
    val tasksInCategory: List<Task> = emptyList(),
    val selectedCategory: String? = null,
    val isLoading: Boolean = true
)

