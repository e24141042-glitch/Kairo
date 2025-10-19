package com.kairo.app.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room database for storing tasks
 */
@Database(
    entities = [Task::class],
    version = 4, // <--- INCREMENT VERSION
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {
    
    abstract fun taskDao(): TaskDao
    
    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null
        
        // --- DEFINE MIGRATION ---
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns for repeat feature
                database.execSQL("ALTER TABLE tasks ADD COLUMN repeatInterval TEXT NOT NULL DEFAULT 'NONE'")
                database.execSQL("ALTER TABLE tasks ADD COLUMN repeatEndDate INTEGER") // Date stored as INTEGER (timestamp)
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a new table with the combined dueDateTime column
                database.execSQL("CREATE TABLE tasks_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL DEFAULT '', isCompleted INTEGER NOT NULL DEFAULT 0, priority TEXT NOT NULL DEFAULT 'MEDIUM', category TEXT NOT NULL DEFAULT 'General', dueDateTime INTEGER, createdAt INTEGER NOT NULL, completedAt INTEGER, isSynced INTEGER NOT NULL DEFAULT 0, googleCalendarEventId TEXT, repeatInterval TEXT NOT NULL DEFAULT 'NONE', repeatEndDate INTEGER)")

                // Copy data from the old table to the new table
                // Combine dueDate and dueTime into dueDateTime if both exist
                database.execSQL("INSERT INTO tasks_new (id, title, description, isCompleted, priority, category, dueDateTime, createdAt, completedAt, isSynced, googleCalendarEventId, repeatInterval, repeatEndDate) SELECT id, title, description, isCompleted, priority, category, CASE WHEN dueDate IS NOT NULL AND dueTime IS NOT NULL THEN (dueDate + (dueTime % 86400000)) ELSE dueDate END, createdAt, completedAt, isSynced, googleCalendarEventId, repeatInterval, repeatEndDate FROM tasks")

                // Remove the old table
                database.execSQL("DROP TABLE tasks")

                // Rename the new table to the old table name
                database.execSQL("ALTER TABLE tasks_new RENAME TO tasks")
            }
        }
        // --- END DEFINE MIGRATION ---

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "kairo_database"
                )
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4) // <--- ADD MIGRATION HERE
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
