package com.example.quicktasks.data

import androidx.room.*
import androidx.room.Query
import com.example.quicktasks.model.Task
import com.example.quicktasks.model.TaskPriority
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Data Access Object for Task entity
 * Provides CRUD operations and various queries for task management
 */
@Dao
interface TaskDao {
    
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC, priority DESC")
    fun getAllTasks(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE completedStatus = 0 ORDER BY dueDate ASC, priority DESC")
    fun getActiveTasks(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE completedStatus = 1 ORDER BY dueDate DESC")
    fun getCompletedTasks(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startDate AND :endDate ORDER BY dueDate ASC, priority DESC")
    fun getTasksByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE dueDate = :date ORDER BY priority DESC")
    fun getTasksByDate(date: LocalDateTime): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY dueDate ASC, priority DESC")
    fun getTasksByCategory(category: String): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY dueDate ASC")
    fun getTasksByPriority(priority: TaskPriority): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY dueDate ASC, priority DESC")
    fun searchTasks(query: String): Flow<List<Task>>
    
    @Query("SELECT DISTINCT category FROM tasks WHERE category != '' ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>
    
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?
    
    @Query("SELECT * FROM tasks WHERE reminderTime IS NOT NULL AND reminderTime <= :currentTime ORDER BY reminderTime ASC")
    fun getTasksWithReminders(currentTime: LocalDateTime): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE isRecurring = 1 ORDER BY dueDate ASC")
    fun getRecurringTasks(): Flow<List<Task>>
    
    @Query("SELECT COUNT(*) FROM tasks WHERE completedStatus = 1 AND dueDate >= :startDate AND dueDate <= :endDate")
    suspend fun getCompletedTasksCount(startDate: LocalDateTime, endDate: LocalDateTime): Int
    
    @Query("SELECT COUNT(*) FROM tasks WHERE completedStatus = 0 AND dueDate < :currentDate")
    suspend fun getOverdueTasksCount(currentDate: LocalDateTime): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long
    
    @Update
    suspend fun updateTask(task: Task)
    
    @Delete
    suspend fun deleteTask(task: Task)
    
    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)
    
    @Query("UPDATE tasks SET completedStatus = :completed WHERE id = :id")
    suspend fun updateTaskCompletion(id: Long, completed: Boolean)
    
    @Query("UPDATE tasks SET streakCount = :streak WHERE id = :id")
    suspend fun updateTaskStreak(id: Long, streak: Int)
    
    @Query("DELETE FROM tasks WHERE completedStatus = 1")
    suspend fun deleteAllCompletedTasks()
}

