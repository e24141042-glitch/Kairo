package com.kairo.app.repository

import com.kairo.app.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date
import com.kairo.app.data.Priority

/**
 * Repository for managing tasks and providing data access abstraction
 */
class TaskRepository(
    private val taskDao: TaskDao,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    
    /**
     * Get all tasks with user preferences applied
     */
    fun getAllTasks(): Flow<List<Task>> {
        return userPreferencesRepository.userPreferences.flatMapLatest { preferences ->
            taskDao.getAllTasks().map { tasks ->
                applySortingAndFiltering(tasks, preferences)
            }
        }
    }
    
    /**
     * Get completed tasks
     */
    fun getCompletedTasks(): Flow<List<Task>> = taskDao.getCompletedTasks()
    
    /**
     * Get pending tasks
     */
    fun getPendingTasks(): Flow<List<Task>> = taskDao.getPendingTasks()
    
    /**
     * Get tasks by priority
     */
    fun getTasksByPriority(priority: Priority): Flow<List<Task>> = 
        taskDao.getTasksByPriority(priority)
    
    /**
     * Get tasks by category
     */
    fun getTasksByCategory(category: String): Flow<List<Task>> = 
        taskDao.getTasksByCategory(category)
    
    /**
     * Search tasks
     */
    fun searchTasks(query: String): Flow<List<Task>> = taskDao.searchTasks(query)
    
    /**
     * Get overdue tasks
     */
    fun getOverdueTasks(): Flow<List<Task>> = taskDao.getOverdueTasks(Date())
    
    /**
     * Get tasks due today
     */
    fun getTasksDueToday(): Flow<List<Task>> = taskDao.getTasksDueToday(Date())
    
    /**
     * Get task by ID
     */
    suspend fun getTaskById(taskId: Long): Task? = taskDao.getTaskById(taskId)
    
    /**
     * Get all categories
     */
    fun getAllCategories(): Flow<List<String>> = taskDao.getAllCategories()
    
    /**
     * Get task statistics
     */
    suspend fun getTaskStatistics(): TaskStatistics {
        val total = taskDao.getTotalTaskCount()
        val completed = taskDao.getCompletedTaskCount()
        val pending = taskDao.getPendingTaskCount()
        
        return TaskStatistics(
            totalTasks = total,
            completedTasks = completed,
            pendingTasks = pending,
            completionPercentage = if (total > 0) (completed.toFloat() / total.toFloat() * 100).toInt() else 0
        )
    }
    
    /**
     * Insert a new task
     */
    suspend fun insertTask(task: Task): Long {
        val taskWithTimestamp = task.copy(
            createdAt = Date(),
            completedAt = null // Ensure completedAt is null for new tasks
        )
        return taskDao.insertTask(taskWithTimestamp)
    }
    
    /**
     * Update an existing task
     */
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }
    
    /**
     * Delete a task
     */
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    
    /**
     * Delete all tasks
     */
    suspend fun deleteAllTasks() = taskDao.deleteAllTasks()
    
    /**
     * Delete completed tasks
     */
    suspend fun deleteCompletedTasks() = taskDao.deleteCompletedTasks()
    
    /**
     * Mark task as completed
     */
    suspend fun markTaskCompleted(taskId: Long) {
        taskDao.markTaskCompleted(taskId, Date())
    }
    
    /**
     * Mark task as pending
     */
    suspend fun markTaskPending(taskId: Long) {
        taskDao.markTaskPending(taskId, null)
    }
    
    /**
     * Toggle task completion status
     */
    suspend fun toggleTaskCompletion(task: Task) {
        if (task.isCompleted) {
            markTaskPending(task.id)
        } else {
            markTaskCompleted(task.id)
            if (task.repeatInterval != RepeatInterval.NONE) {
                generateNextRecurringTask(task)
            }
        }
    }

    /**
     * Generates the next instance of a recurring task.
     */
    private suspend fun generateNextRecurringTask(completedTask: Task) {
        if (completedTask.repeatInterval == RepeatInterval.NONE || completedTask.dueDateTime == null) {
            return
        }

        val nextDueDateTime = calculateNextOccurrence(completedTask.dueDateTime, completedTask.repeatInterval)

        if (nextDueDateTime != null && (completedTask.repeatEndDate == null || nextDueDateTime.before(completedTask.repeatEndDate) || nextDueDateTime == completedTask.repeatEndDate)) {
            val newRecurringTask = completedTask.copy(
                id = 0L, // New task, so id is 0
                dueDateTime = nextDueDateTime,
                isCompleted = false,
                createdAt = Date(),
                completedAt = null
            )
            insertTask(newRecurringTask)
        }
    }

    /**
     * Calculates the next occurrence date based on the current date and repeat interval.
     */
    private fun calculateNextOccurrence(currentDate: Date, interval: RepeatInterval): Date? {
        val calendar = Calendar.getInstance().apply { time = currentDate }
        return when (interval) {
            RepeatInterval.DAILY -> {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                calendar.time
            }
            RepeatInterval.WEEKLY -> {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                calendar.time
            }
            RepeatInterval.MONTHLY -> {
                calendar.add(Calendar.MONTH, 1)
                calendar.time
            }
            RepeatInterval.YEARLY -> {
                calendar.add(Calendar.YEAR, 1)
                calendar.time
            }
            RepeatInterval.NONE -> null
        }
    }
    
    /**
     * Apply user preferences for sorting and filtering
     */
    private suspend fun applySortingAndFiltering(
        tasks: List<Task>,
        preferences: UserPreferences
    ): List<Task> {
        var filteredTasks = tasks
        
        // Filter completed tasks if needed
        if (!preferences.showCompletedTasks) {
            filteredTasks = filteredTasks.filter { !it.isCompleted }
        }
        
        // Apply sorting
        filteredTasks = when (preferences.sortOrder) {
            UserPreferences.SortOrder.DATE_CREATED -> 
                filteredTasks.sortedByDescending { it.createdAt }
            UserPreferences.SortOrder.DATE_DUE -> 
                filteredTasks.sortedWith(compareBy<Task> { it.dueDateTime == null }.thenBy { it.dueDateTime })
            UserPreferences.SortOrder.PRIORITY -> 
                filteredTasks.sortedByDescending { it.priority.ordinal }
            UserPreferences.SortOrder.TITLE -> 
                filteredTasks.sortedBy { it.title.lowercase() }
            UserPreferences.SortOrder.CATEGORY -> 
                filteredTasks.sortedBy { it.category }
        }
        
        return filteredTasks
    }
}

/**
 * Data class for task statistics
 */
data class TaskStatistics(
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val completionPercentage: Int
)
