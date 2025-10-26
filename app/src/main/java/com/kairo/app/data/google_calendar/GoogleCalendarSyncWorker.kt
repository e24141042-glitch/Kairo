package com.kairo.app.data.google_calendar

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kairo.app.di.AppContainer
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.flow.first
import com.kairo.app.data.UserPreferences

class GoogleCalendarSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("GoogleCalendarSyncWorker", "Starting Google Calendar sync work...")
        
        // Set sync status to SYNCING
        AppContainer.userPreferencesRepository.updateSyncStatus(UserPreferences.SyncStatus.SYNCING)
        AppContainer.userPreferencesRepository.updateSyncErrorMessage(null)
        
        return try {
            val prefs = AppContainer.userPreferencesRepository.userPreferences.first()

            // Ensure the Calendar service is initialized if the user is signed in
            val lastAccount = GoogleSignIn.getLastSignedInAccount(applicationContext)
            if (lastAccount != null && lastAccount.email == prefs.googleCalendarAccount) {
                AppContainer.googleCalendarService.initialize(lastAccount)
            }

            // If calendar ID is missing, attempt to create or fetch the Kairo calendar
            if (prefs.googleCalendarId == null) {
                Log.d("GoogleCalendarSyncWorker", "Calendar ID missing; attempting to create/fetch Kairo calendar.")
                AppContainer.googleCalendarService.createKairoCalendar()
            }

            val googleCalendarService = AppContainer.googleCalendarService

            // PULL first: reconcile changes from Google Calendar into local tasks
            googleCalendarService.syncGoogleCalendarToTasks()

            // PUSH: mirror local tasks into Google Calendar after local has been updated
            val localTasks = AppContainer.taskRepository.getAllTasks().first()
            googleCalendarService.syncTasksToGoogleCalendar(localTasks)

            Log.d("GoogleCalendarSyncWorker", "Google Calendar sync operation completed.")
            
            // Set sync status to SUCCESS and update last sync time
            AppContainer.userPreferencesRepository.updateSyncStatus(UserPreferences.SyncStatus.SUCCESS)
            AppContainer.userPreferencesRepository.updateLastSyncTime(System.currentTimeMillis())
            
            return Result.success()
        } catch (e: Exception) {
            Log.e("GoogleCalendarSyncWorker", "Error during Google Calendar sync work: ${e.message}", e)
            
            // Set sync status to ERROR and store error message
            AppContainer.userPreferencesRepository.updateSyncStatus(UserPreferences.SyncStatus.ERROR)
            AppContainer.userPreferencesRepository.updateSyncErrorMessage(e.message ?: "Unknown sync error")
            
            Result.failure()
        }
    }
}