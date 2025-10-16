package com.kairo.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

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
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY updatedAt DESC")
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
    fun getTasksByPriority(priority: Task.Priority): Flow<List<Task>>
    
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
    @Query("SELECT * FROM tasks WHERE dueDate < datetime('now') AND isCompleted = 0 ORDER BY dueDate ASC")
    fun getOverdueTasks(): Flow<List<Task>>
    
    /**
     * Get tasks due today
     */
    @Query("SELECT * FROM tasks WHERE date(dueDate) = date('now') AND isCompleted = 0 ORDER BY dueDate ASC")
    fun getTasksDueToday(): Flow<List<Task>>
    
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
    @Query("UPDATE tasks SET isCompleted = 1, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun markTaskCompleted(taskId: Long, updatedAt: String)
    
    /**
     * Mark task as pending
     */
    @Query("UPDATE tasks SET isCompleted = 0, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun markTaskPending(taskId: Long, updatedAt: String)
}
