package com.example.quicktasks.data

import androidx.room.TypeConverter
import com.example.quicktasks.model.TaskPriority
import com.example.quicktasks.model.RecurringType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Type converters for Room database
 * Handles conversion between custom types and database-compatible types
 */
class Converters {
    
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }
    
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }
    
    @TypeConverter
    fun fromTaskPriority(priority: TaskPriority): String {
        return priority.name
    }
    
    @TypeConverter
    fun toTaskPriority(priority: String): TaskPriority {
        return TaskPriority.valueOf(priority)
    }
    
    @TypeConverter
    fun fromRecurringType(type: RecurringType?): String? {
        return type?.name
    }
    
    @TypeConverter
    fun toRecurringType(type: String?): RecurringType? {
        return type?.let { RecurringType.valueOf(it) }
    }
}

