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

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.android.gms.common.api.Scope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.services.calendar.model.EventReminder

// --- RE-ADD IMPORTS ---
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.extensions.android.http.AndroidHttp
// --- END RE-ADD IMPORTS ---

class GoogleCalendarService(
    private val context: Context,
    private val taskRepository: TaskRepository
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
        // --- REVERT TO GoogleAccountCredential ---
        credential = GoogleAccountCredential.usingOAuth2(
            context, Collections.singleton(CalendarScopes.CALENDAR)
        ).setSelectedAccount(googleSignInAccount.account)

        calendarService = Calendar.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Kairo Task Management").build()
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

    // Placeholder for syncing tasks to Google Calendar
    fun syncTasksToGoogleCalendar(tasks: List<Task>) {
        // This function will take a list of Kairo tasks and
        // convert them into Google Calendar events, then insert/update them.
        println("Syncing ${tasks.size} tasks to Google Calendar (placeholder)...")
    }

    // Function to sync Google Calendar events to Kairo tasks
    suspend fun syncGoogleCalendarToTasks() {
        if (calendarService == null) {
            Log.e("GoogleCalendarService", "Calendar service not initialized. Cannot sync.")
            return
        }

        Log.d("GoogleCalendarService", "Starting sync from Kairo tasks to Google Calendar...")

        withContext(Dispatchers.IO) {
            try {
                val kairoTasks = taskRepository.getAllTasks().first() // Get all tasks from Flow
                Log.d("GoogleCalendarService", "Fetched ${kairoTasks.size} Kairo tasks.")

                kairoTasks.forEach { kairoTask ->
                    val event = createGoogleCalendarEvent(kairoTask)
                    // For simplicity, we\'ll just insert new events.
                    // A more robust solution would check for existing events and update them.
                    val insertedEvent = calendarService?.events()?.insert("primary", event)?.execute()
                    Log.d("GoogleCalendarService", "Inserted event: ${insertedEvent?.summary} (ID: ${insertedEvent?.id})")
                }
                Log.d("GoogleCalendarService", "Sync to Google Calendar completed.")
            } catch (e: Exception) {
                Log.e("GoogleCalendarService", "Error syncing tasks to Google Calendar: ${e.message}", e)
            } catch (e: IOException) {
                Log.e("GoogleCalendarService", "Network error during sync: ${e.message}", e)
            }
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
