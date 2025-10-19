package com.kairo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kairo.app.data.UserPreferences
import com.kairo.app.ui.screens.AddEditTaskScreen
import com.kairo.app.ui.screens.HomeScreen
import com.kairo.app.ui.screens.SettingsScreen
import com.kairo.app.ui.theme.KairoTheme
import com.kairo.app.di.AppContainer

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import android.util.Log
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kairo.app.data.google_calendar.GoogleCalendarSyncWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        googleSignInLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            val data: Intent? = result.data
            AppContainer.googleCalendarService.handleSignInResult(
                data,
                onSuccess = { email ->
                    Log.d("MainActivity", "Google Sign-In successful for: $email")
                    lifecycleScope.launch {
                        AppContainer.userPreferencesRepository.updateGoogleCalendarAccount(email)
                    }
                },
                onFailure = {
                    Log.e("MainActivity", "Google Sign-In failed.")
                    // TODO: Show error message to user
                }
            )
        }

        setContent {
            val prefs by AppContainer.userPreferencesRepository
                .userPreferences
                .collectAsState(initial = UserPreferences())

            KairoTheme(darkTheme = prefs.isDarkTheme, dynamicColor = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KairoApp(
                        onSignInGoogleCalendar = {
                            val signInIntent = AppContainer.googleCalendarService.getSignInIntent()
                            googleSignInLauncher.launch(signInIntent)
                        }
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch {
            AppContainer.userPreferencesRepository.userPreferences.collect { prefs ->
                prefs.googleCalendarAccount?.let { accountName ->
                    Log.d("MainActivity", "App stopping, enqueuing Google Calendar sync work...")
                    val syncWorkRequest = OneTimeWorkRequestBuilder<GoogleCalendarSyncWorker>()
                        .setInitialDelay(10, TimeUnit.SECONDS) // Delay to allow app to close gracefully
                        .build()
                    WorkManager.getInstance(applicationContext).enqueue(syncWorkRequest)
                }
            }
        }
    }
}

@Composable
fun KairoApp(
    onSignInGoogleCalendar: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToAddTask = {
                    navController.navigate("add_task")
                },
                onNavigateToEditTask = { taskId ->
                    navController.navigate("edit_task/$taskId")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }

        composable("add_task") {
            AddEditTaskScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("edit_task/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toLongOrNull() ?: 0L
            AddEditTaskScreen(
                taskId = taskId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSignInGoogleCalendar = onSignInGoogleCalendar
            )
        }
    }
}
