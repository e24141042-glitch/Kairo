package com.kairo.app.data

import androidx.room.TypeConverter
import java.util.Date
import com.kairo.app.data.Priority

/**
 * Type converters for Room database to handle Date, and RepeatInterval
 */
class Converters {
    
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }
    
    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.valueOf(priority)
    }

    @TypeConverter
    fun fromRepeatInterval(repeatInterval: RepeatInterval): String {
        return repeatInterval.name
    }

    @TypeConverter
    fun toRepeatInterval(repeatInterval: String): RepeatInterval {
        return RepeatInterval.valueOf(repeatInterval)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
