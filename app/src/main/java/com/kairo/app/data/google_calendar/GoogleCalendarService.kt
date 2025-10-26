package com.kairo.app.data.google_calendar

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.client.util.DateTime
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kairo.app.data.Task
import com.kairo.app.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Collections
import java.util.Date

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.android.gms.common.api.Scope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.services.calendar.model.EventReminder
import com.kairo.app.data.UserPreferencesRepository

class GoogleCalendarService(
    private val context: Context,
    private val taskRepository: TaskRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {

    private var credential: GoogleAccountCredential? = null
    private var calendarService: Calendar? = null

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(CalendarScopes.CALENDAR))
            .requestServerAuthCode("719369528057-so89e932hs9phvj6apv6htcpkli4h745.apps.googleusercontent.com")
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    // Initialize Google Account Credential and Calendar Service
    fun initialize(googleSignInAccount: GoogleSignInAccount) {
        Log.d("GoogleCalendarService", "Initializing Google Calendar service.")
        // --- REVERT TO GoogleAccountCredential ---
        credential = GoogleAccountCredential.usingOAuth2(
            context, Collections.singleton(CalendarScopes.CALENDAR)
        ).setSelectedAccount(googleSignInAccount.account)

        calendarService = Calendar.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Kairo Task Management").build()
        Log.d("GoogleCalendarService", "Calendar service initialized: ${calendarService != null}")
    }

    // Placeholder for authentication flow (e.g., launching Google Sign-In intent)
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleSignInResult(data: Intent?, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
            account.email?.let { email ->
                initialize(account)
                onSuccess(email)
            } ?: onFailure()
        } catch (e: com.google.android.gms.common.api.ApiException) {
            onFailure()
        }
    }


    suspend fun createKairoCalendar() {
        Log.d("GoogleCalendarService", "createKairoCalendar called.")
        if (calendarService == null) {
            Log.e("GoogleCalendarService", "Calendar service not initialized. Cannot create calendar.")
            return
        }

        withContext(Dispatchers.IO) {
            try {
                Log.d("GoogleCalendarService", "Attempting to list calendars.")
                val calendars = calendarService?.calendarList()?.list()?.execute()
                Log.d("GoogleCalendarService", "Calendars listed. Found ${calendars?.items?.size ?: 0} calendars.")
                val kairoCalendar = calendars?.items?.find { it.summary == "Kairo Tasks" }

                if (kairoCalendar == null) {
                    Log.d("GoogleCalendarService", "Kairo Tasks calendar not found, creating new one.")
                    val newCalendar = com.google.api.services.calendar.model.Calendar().setSummary("Kairo Tasks")
                    val createdCalendar = calendarService?.calendars()?.insert(newCalendar)?.execute()
                    createdCalendar?.id?.let {
                        userPreferencesRepository.updateGoogleCalendarId(it)
                        Log.d("GoogleCalendarService", "Created Kairo Tasks calendar with ID: $it")
                    } ?: Log.e("GoogleCalendarService", "Failed to get ID of created calendar. createdCalendar was null.")
                } else {
                    userPreferencesRepository.updateGoogleCalendarId(kairoCalendar.id)
                    Log.d("GoogleCalendarService", "Found existing Kairo Tasks calendar with ID: ${kairoCalendar.id}")
                }
            } catch (e: Exception) {
                Log.e("GoogleCalendarService", "Error creating Kairo calendar: ${e.message}", e)
            }
        }
    }

    private suspend fun getKairoCalendarId(): String? {
        val id = userPreferencesRepository.userPreferences.first().googleCalendarId
        Log.d("GoogleCalendarService", "Retrieved Kairo calendar ID: $id")
        return id
    }

    suspend fun syncTasksToGoogleCalendar(tasks: List<Task>) {
        if (calendarService == null) {
            Log.e("GoogleCalendarService", "Calendar service not initialized. Cannot sync.")
            return
        }

        val calendarId = getKairoCalendarId()
        if (calendarId == null) {
            Log.e("GoogleCalendarService", "Kairo calendar ID not found. Cannot sync.")
            return
        }

        withContext(Dispatchers.IO) {
            try {
                val events = calendarService?.events()?.list(calendarId)?.execute()
                val eventIds = events?.items?.map { it.id } ?: emptyList()
                val taskIds = tasks.map { it.googleCalendarEventId }

                // Do not delete events that are not linked to local tasks.
                // External or newly created events will be handled by pull sync.

                tasks.forEach { task ->
                    if (task.googleCalendarEventId == null) {
                        val event = createGoogleCalendarEvent(task)
                        val insertedEvent = calendarService?.events()?.insert(calendarId, event)?.execute()
                        insertedEvent?.id?.let {
                            taskRepository.updateTask(task.copy(googleCalendarEventId = it))
                        }
                    } else {
                        try {
                            val existingEvent = calendarService?.events()?.get(calendarId, task.googleCalendarEventId)?.execute()
                            if (existingEvent != null) {
                                val updatedEvent = createGoogleCalendarEvent(task)
                                if (existingEvent.summary != updatedEvent.summary || 
                                    existingEvent.description != updatedEvent.description ||
                                    existingEvent.start?.dateTime?.value != task.dueDateTime?.time) {
                                    calendarService?.events()?.update(calendarId, task.googleCalendarEventId, updatedEvent)?.execute()
                                }
                            } else {
                                // Event doesn't exist anymore, create a new one
                                val event = createGoogleCalendarEvent(task)
                                val insertedEvent = calendarService?.events()?.insert(calendarId, event)?.execute()
                                insertedEvent?.id?.let {
                                    taskRepository.updateTask(task.copy(googleCalendarEventId = it))
                                }
                            }
                        } catch (e: Exception) {
                            // Event not found or other error, create a new one
                            Log.w("GoogleCalendarService", "Event ${task.googleCalendarEventId} not found, creating new one: ${e.message}")
                            val event = createGoogleCalendarEvent(task)
                            val insertedEvent = calendarService?.events()?.insert(calendarId, event)?.execute()
                            insertedEvent?.id?.let {
                                taskRepository.updateTask(task.copy(googleCalendarEventId = it))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("GoogleCalendarService", "Error syncing tasks to Google Calendar: ${e.message}", e)
            }
        }
    }

    suspend fun syncGoogleCalendarToTasks() {
        if (calendarService == null) {
            Log.e("GoogleCalendarService", "Calendar service not initialized. Cannot sync.")
            return
        }

        val calendarId = getKairoCalendarId()
        if (calendarId == null) {
            Log.e("GoogleCalendarService", "Kairo calendar ID not found. Cannot sync.")
            return
        }

        withContext(Dispatchers.IO) {
            try {
                val tasks = taskRepository.getAllTasks().first()
                val taskIds = tasks.map { it.googleCalendarEventId }
                val events = calendarService?.events()?.list(calendarId)?.execute()
                val eventIds = events?.items?.map { it.id } ?: emptyList()

                taskIds.forEach { taskId ->
                    if (taskId != null && taskId !in eventIds) {
                        taskRepository.getTaskByGoogleCalendarEventId(taskId).first()?.let {
                            taskRepository.deleteTask(it)
                        }
                    }
                }

                events?.items?.forEach { event ->
                    val task = taskRepository.getTaskByGoogleCalendarEventId(event.id).first()
                    if (task == null) {
                        val newTask = Task(
                            title = event.summary,
                            description = event.description ?: "",
                            dueDateTime = event.start?.dateTime?.value?.let { Date(it) },
                            googleCalendarEventId = event.id
                        )
                        taskRepository.insertTask(newTask)
                    } else {
                        if (task.title != event.summary || task.description != event.description || task.dueDateTime?.time != event.start?.dateTime?.value) {
                            val updatedTask = task.copy(
                                title = event.summary,
                                description = event.description ?: "",
                                dueDateTime = event.start?.dateTime?.value?.let { Date(it) }
                            )
                            taskRepository.updateTask(updatedTask)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("GoogleCalendarService", "Error syncing Google Calendar to tasks: ${e.message}", e)
            }
        }
    }

    suspend fun deleteEventFromGoogleCalendar(eventId: String) {
        if (calendarService == null) {
            Log.e("GoogleCalendarService", "Calendar service not initialized. Cannot delete event.")
            return
        }

        val calendarId = getKairoCalendarId()
        if (calendarId == null) {
            Log.e("GoogleCalendarService", "Kairo calendar ID not found. Cannot delete event.")
            return
        }

        withContext(Dispatchers.IO) {
            try {
                calendarService?.events()?.delete(calendarId, eventId)?.execute()
            } catch (e: Exception) {
                Log.e("GoogleCalendarService", "Error deleting event from Google Calendar: ${e.message}", e)
            }
        }
    }

    suspend fun deleteTaskFromDatabase(eventId: String) {
        val task = taskRepository.getTaskByGoogleCalendarEventId(eventId).first()
        if (task != null) {
            taskRepository.deleteTask(task)
        }
    }

    // Example of how to create an event (will be used in syncTasksToGoogleCalendar)
    private fun createGoogleCalendarEvent(task: Task): Event {
        val event = Event()
            .setSummary(task.title)
            .setDescription(task.description)

        task.dueDateTime?.let {
            val startDateTime = DateTime(it) // Use the Date object directly
            val endDateTime = DateTime(Date(it.time + 3600000)) // Assuming 1 hour duration for simplicity
            event.start = EventDateTime().setDateTime(startDateTime)
            event.end = EventDateTime().setDateTime(endDateTime)

            // Add reminders
            val reminders = Event.Reminders().apply {
                useDefault = false
                overrides = listOf(
                    EventReminder().setMethod("popup").setMinutes(24 * 60), // 1 day before
                    EventReminder().setMethod("popup").setMinutes(60) // 1 hour before
                )
            }
            event.reminders = reminders
        }

        return event
    }
}

