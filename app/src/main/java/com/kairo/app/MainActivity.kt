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
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.kairo.app.di.AppContainer.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            val prefs by AppContainer.userPreferencesRepository
                .userPreferences
                .collectAsState(initial = UserPreferences())

            KairoTheme(darkTheme = prefs.isDarkTheme, dynamicColor = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KairoApp()
                }
            }
        }
    }
}

@Composable
fun KairoApp() {
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
                }
            )
        }
    }
}
