package com.noteflow.app.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.noteflow.app.features.home.presentation.HomeScreen
import com.noteflow.app.features.notes.presentation.screens.NoteDetailScreen
import com.noteflow.app.features.notes.presentation.screens.NoteListScreen
import com.noteflow.app.features.search.presentation.SearchScreen
import com.noteflow.app.features.settings.presentation.screens.SettingsScreen
import com.noteflow.app.features.stats.presentation.screens.StatsScreen
import com.noteflow.app.features.tasks.presentation.screens.TaskListScreen
import com.noteflow.app.features.timer.presentation.TimerViewModel
import com.noteflow.app.features.timer.presentation.screens.TimerScreen

private val BgColor = Color(0xFF131313)

@Composable
fun AppNavigation(
    isFirstTime: Boolean = false,
    onOnboardingFinished: () -> Unit = {}
) {
    val navController = rememberNavController()
    val timerViewModel: TimerViewModel = hiltViewModel()

    Scaffold(containerColor = BgColor) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                HomeScreen(
                    onNoteClick = { id -> navController.navigate("note/$id") },
                    onAddNote = { navController.navigate("note/0") },
                    onNavigateToTimer = { navController.navigate("timer") },
                    onNavigateToTasks = { navController.navigate("tasks") },
                    onNavigateToNotes = { navController.navigate("notes") },
                    onNavigateToStats = { navController.navigate("stats") },
                    onNavigateToSettings = { navController.navigate("settings") },
                    onNavigateToSearch = { navController.navigate("search") },
                    timerViewModel = timerViewModel
                )
            }
            composable("notes") {
                NoteListScreen(
                    onNoteClick = { id -> navController.navigate("note/$id") },
                    onAddNote = { navController.navigate("note/0") }
                )
            }
            composable(
                route = "note/{noteId}",
                arguments = listOf(navArgument("noteId") { type = NavType.LongType })
            ) { backStack ->
                val id = backStack.arguments?.getLong("noteId") ?: 0L
                NoteDetailScreen(
                    noteId = id,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNote = { targetId -> navController.navigate("note/$targetId") }
                )
            }
            composable("tasks") {
                TaskListScreen(onNavigateToNote = { id -> navController.navigate("note/$id") })
            }
            composable("timer") {
                TimerScreen(
                    onNavigateBack = { navController.popBackStack() },
                    timerViewModel = timerViewModel
                )
            }
            composable("stats") { StatsScreen() }
            composable("settings") { SettingsScreen() }
            composable("search") {
                SearchScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNoteClick = { id -> navController.navigate("note/$id") }
                )
            }
        }
    }
}
