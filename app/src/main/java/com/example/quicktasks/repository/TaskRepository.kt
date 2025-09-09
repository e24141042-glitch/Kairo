package com.example.quicktasks.repository

import com.example.quicktasks.data.TaskDao
import com.example.quicktasks.model.Task
import com.example.quicktasks.model.TaskPriority
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository pattern implementation for Task data access
 * Provides a clean interface between ViewModels and data layer
 */
@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    
    fun getActiveTasks(): Flow<List<Task>> = taskDao.getActiveTasks()
    
    fun getCompletedTasks(): Flow<List<Task>> = taskDao.getCompletedTasks()
    
    fun getTasksByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Task>> =
        taskDao.getTasksByDateRange(startDate, endDate)
    
    fun getTasksByDate(date: LocalDateTime): Flow<List<Task>> = taskDao.getTasksByDate(date)
    
    fun getTasksByCategory(category: String): Flow<List<Task>> = taskDao.getTasksByCategory(category)
    
    fun getTasksByPriority(priority: TaskPriority): Flow<List<Task>> = taskDao.getTasksByPriority(priority)
    
    fun searchTasks(query: String): Flow<List<Task>> = taskDao.searchTasks(query)
    
    fun getAllCategories(): Flow<List<String>> = taskDao.getAllCategories()
    
    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)
    
    fun getTasksWithReminders(currentTime: LocalDateTime): Flow<List<Task>> = 
        taskDao.getTasksWithReminders(currentTime)
    
    fun getRecurringTasks(): Flow<List<Task>> = taskDao.getRecurringTasks()
    
    suspend fun getCompletedTasksCount(startDate: LocalDateTime, endDate: LocalDateTime): Int =
        taskDao.getCompletedTasksCount(startDate, endDate)
    
    suspend fun getOverdueTasksCount(currentDate: LocalDateTime): Int =
        taskDao.getOverdueTasksCount(currentDate)
    
    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)
    
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    
    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)
    
    suspend fun updateTaskCompletion(id: Long, completed: Boolean) = 
        taskDao.updateTaskCompletion(id, completed)
    
    suspend fun updateTaskStreak(id: Long, streak: Int) = taskDao.updateTaskStreak(id, streak)
    
    suspend fun deleteAllCompletedTasks() = taskDao.deleteAllCompletedTasks()
}

