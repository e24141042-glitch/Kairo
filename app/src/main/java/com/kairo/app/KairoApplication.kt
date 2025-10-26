package com.kairo.app

import android.app.Application
import com.kairo.app.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import com.google.android.gms.auth.api.signin.GoogleSignIn

/**
 * Application class for Kairo To-Do App
 */
class KairoApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        AppContainer.init(applicationContext)

        applicationScope.launch {
            val prefs = AppContainer.userPreferencesRepository.userPreferences.firstOrNull()
            prefs?.googleCalendarAccount?.let { accountName ->
                val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(applicationContext)
                if (googleSignInAccount != null && googleSignInAccount.email == accountName) {
                    AppContainer.googleCalendarService.initialize(googleSignInAccount)
                    // Ensure the calendar ID exists; create or fetch it if missing
                    if (prefs.googleCalendarId == null) {
                        AppContainer.googleCalendarService.createKairoCalendar()
                    }
                }
            }
        }
    }
}
