package com.kairo.app.data.google_calendar

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kairo.app.di.AppContainer
import android.util.Log

class GoogleCalendarSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("GoogleCalendarSyncWorker", "Starting Google Calendar sync work...")
        return try {
            // Ensure AppContainer is initialized (it should be by KairoApplication)
            // If not, this might cause issues, but for now, we assume it's ready.
            val googleCalendarService = AppContainer.googleCalendarService
            googleCalendarService.syncGoogleCalendarToTasks()
            Log.d("GoogleCalendarSyncWorker", "Google Calendar sync operation completed.")
            Result.success()
        } catch (e: Exception) {
            Log.e("GoogleCalendarSyncWorker", "Error during Google Calendar sync work: ${e.message}", e)
            Result.failure()
        }
    }
}