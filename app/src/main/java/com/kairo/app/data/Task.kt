package com.kairo.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Task entity representing a to-do item in the database
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val category: String = "General",
    val dueDate: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val reminderTime: LocalDateTime? = null
) {
    /**
     * Priority levels for tasks
     */
    enum class Priority(val displayName: String) {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        URGENT("Urgent")
    }
    
    /**
     * Check if task is overdue
     */
    val isOverdue: Boolean
        get() = dueDate?.let { it.isBefore(LocalDateTime.now()) && !isCompleted } ?: false
    
    /**
     * Get formatted due date string
     */
    val formattedDueDate: String?
        get() = dueDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
    
    /**
     * Get formatted creation date string
     */
    val formattedCreatedAt: String
        get() = createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
}
