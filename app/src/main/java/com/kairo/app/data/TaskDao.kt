package com.kairo.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Data Access Object for Task entity
 * Provides methods to interact with the tasks table
 */
@Dao
interface TaskDao {
    
    /**
     * Get all tasks ordered by creation date (newest first)
     */
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>
    
    /**
     * Get completed tasks
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTasks(): Flow<List<Task>>
    
    /**
     * Get pending tasks
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getPendingTasks(): Flow<List<Task>>
    
    /**
     * Get tasks by priority
     */
    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY createdAt DESC")
    fun getTasksByPriority(priority: Priority): Flow<List<Task>>
    
    /**
     * Get tasks by category
     */
    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY createdAt DESC")
    fun getTasksByCategory(category: String): Flow<List<Task>>
    
    /**
     * Search tasks by title or description
     */
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchTasks(query: String): Flow<List<Task>>
    
    /**
     * Get overdue tasks
     */
    @Query("SELECT * FROM tasks WHERE dueDateTime < :currentDate AND isCompleted = 0 ORDER BY dueDateTime ASC")
    fun getOverdueTasks(currentDate: Date): Flow<List<Task>>
    
    /**
     * Get tasks due today
     */
    @Query("SELECT * FROM tasks WHERE strftime('%Y-%m-%d', datetime(dueDateTime / 1000, 'unixepoch')) = strftime('%Y-%m-%d', datetime(:currentDate / 1000, 'unixepoch')) AND isCompleted = 0 ORDER BY dueDateTime ASC")
    fun getTasksDueToday(currentDate: Date): Flow<List<Task>>
    
    /**
     * Get task by ID
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): Task?
    
    /**
     * Get all categories
     */
    @Query("SELECT DISTINCT category FROM tasks WHERE category IS NOT NULL AND category != '' ORDER BY category")
    fun getAllCategories(): Flow<List<String>>
    
    /**
     * Get task statistics
     */
    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTotalTaskCount(): Int
    
    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    suspend fun getCompletedTaskCount(): Int
    
    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    suspend fun getPendingTaskCount(): Int
    
    /**
     * Insert a new task
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long
    
    /**
     * Update an existing task
     */
    @Update
    suspend fun updateTask(task: Task)
    
    /**
     * Delete a task
     */
    @Delete
    suspend fun deleteTask(task: Task)
    
    /**
     * Delete all tasks
     */
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
    
    /**
     * Delete completed tasks
     */
    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()
    
    /**
     * Mark task as completed
     */
    @Query("UPDATE tasks SET isCompleted = 1, completedAt = :completedAt WHERE id = :taskId")
    suspend fun markTaskCompleted(taskId: Long, completedAt: Date?)
    
    /**
     * Mark task as pending
     */
    @Query("UPDATE tasks SET isCompleted = 0, completedAt = :completedAt WHERE id = :taskId")
    suspend fun markTaskPending(taskId: Long, completedAt: Date?)
}
