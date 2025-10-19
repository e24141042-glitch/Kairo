package com.kairo.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.kairo.app.data.Priority

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
    val dueDateTime: Date? = null,
    val createdAt: Date = Date(),
    val completedAt: Date? = null,
    val isSynced: Boolean = false,
    val googleCalendarEventId: String? = null,
    val repeatInterval: RepeatInterval = RepeatInterval.NONE,
    val repeatEndDate: Date? = null
) {
    /**
     * Check if task is overdue
     */
    val isOverdue: Boolean
        get() = dueDateTime?.let { it.before(Date()) && !isCompleted } ?: false
    
    /**
     * Get formatted due date and time string
     */
    val formattedDueDateTime: String?
        get() = dueDateTime?.let { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(it) }
    
    /**
     * Get formatted creation date string
     */
    val formattedCreatedAt: String
        get() = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(createdAt)
}

