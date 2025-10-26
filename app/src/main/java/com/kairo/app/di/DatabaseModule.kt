package com.kairo.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.kairo.app.data.TaskDatabase
import com.kairo.app.data.UserPreferencesRepository
import com.kairo.app.repository.TaskRepository
import com.kairo.app.data.google_calendar.GoogleCalendarService

/**
 * Lightweight app container for manual dependency provisioning (no Hilt runtime requirement).
 */
object AppContainer {
    private var initialized = false

    private lateinit var applicationContext: Context
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var database: TaskDatabase

    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set

    lateinit var taskRepository: TaskRepository
        private set

    lateinit var googleCalendarService: GoogleCalendarService
        private set

    val context: Context
        get() = applicationContext

    private val Context.ds by preferencesDataStore(name = "user_preferences")

    fun init(context: Context) {
        if (initialized) return
        applicationContext = context.applicationContext
        dataStore = applicationContext.ds
        database = Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java,
            "kairo_database"
        ).fallbackToDestructiveMigration().build()

        userPreferencesRepository = UserPreferencesRepository(dataStore)
        taskRepository = TaskRepository(database.taskDao(), userPreferencesRepository)
        googleCalendarService = GoogleCalendarService(applicationContext, taskRepository, userPreferencesRepository)

        initialized = true
    }
}
