package com.example.quicktasks.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Task entity representing a todo item in the database
 * Contains all necessary fields for task management
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: LocalDateTime? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val category: String = "General",
    val completedStatus: Boolean = false,
    val creationDate: LocalDateTime = LocalDateTime.now(),
    val reminderTime: LocalDateTime? = null,
    val isRecurring: Boolean = false,
    val recurringType: RecurringType? = null,
    val streakCount: Int = 0
)

/**
 * Enum representing task priority levels
 */
enum class TaskPriority(val displayName: String, val colorValue: Long) {
    LOW("Low", 0xFF4CAF50),      // Green
    MEDIUM("Medium", 0xFFFF9800), // Orange
    HIGH("High", 0xFFF44336),     // Red
    URGENT("Urgent", 0xFF9C27B0)  // Purple
}

/**
 * Enum representing recurring task types
 */
enum class RecurringType(val displayName: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}

