package com.example.quicktasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.quicktasks.ui.navigation.QuickTasksNavigation
import com.example.quicktasks.ui.theme.QuickTasksTheme
import com.example.quicktasks.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for the QuickTasks app
 * Sets up navigation and theme
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickTasksApp()
        }
    }
}

@Composable
fun QuickTasksApp() {
    val themeViewModel: ThemeViewModel = hiltViewModel()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    
    QuickTasksTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            QuickTasksNavigation(navController = navController)
        }
    }
}
