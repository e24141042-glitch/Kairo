package com.kairo.app.data

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Type converters for Room database to handle LocalDateTime
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
    fun fromPriority(priority: Task.Priority): String {
        return priority.name
    }
    
    @TypeConverter
    fun toPriority(priority: String): Task.Priority {
        return Task.Priority.valueOf(priority)
    }
}
