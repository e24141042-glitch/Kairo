package com.example.quicktasks.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Sealed class defining all app screens and their navigation routes
 */
sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object Home : Screen("home")
    
    object AddTask : Screen("add_task")
    
    object EditTask : Screen(
        route = "edit_task/{taskId}",
        arguments = listOf(
            navArgument("taskId") {
                type = NavType.LongType
            }
        )
    ) {
        fun createRoute(taskId: Long) = "edit_task/$taskId"
    }
    
    object TaskDetails : Screen(
        route = "task_details/{taskId}",
        arguments = listOf(
            navArgument("taskId") {
                type = NavType.LongType
            }
        )
    ) {
        fun createRoute(taskId: Long) = "task_details/$taskId"
    }
    
    object Categories : Screen("categories")
    
    object Settings : Screen("settings")
}

