package com.noteflow.app.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.noteflow.app.features.notes.presentation.NoteViewModel
import com.noteflow.app.features.notes.presentation.screens.NoteDetailScreen
import com.noteflow.app.features.notes.presentation.screens.NoteListScreen
import com.noteflow.app.features.search.presentation.SearchScreen
import com.noteflow.app.features.settings.presentation.screens.SettingsScreen
import com.noteflow.app.features.stats.presentation.screens.StatsScreen
import com.noteflow.app.features.tasks.presentation.screens.TaskListScreen
import com.noteflow.app.features.timer.presentation.screens.TimerScreen

@Composable
fun AppNavigation(
    isFirstTime: Boolean = false,
    onOnboardingFinished: () -> Unit = {}
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            val viewModel: NoteViewModel = hiltViewModel()
            val notes by viewModel.notes.collectAsState()
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFF131313)),
                contentAlignment = Alignment.Center
            ) {
                Text("الرئيسية ✅ — ${notes.size} ملاحظة",
                    color = Color.White, fontSize = 20.sp)
            }
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
                onBack = { navController.popBackStack() },
                onNavigateToNote = { targetId -> navController.navigate("note/$targetId") }
            )
        }
        composable("tasks") {
            TaskListScreen(
                onNavigateToNote = { id -> navController.navigate("note/$id") }
            )
        }
        composable("timer") { TimerScreen() }
        composable("stats") { StatsScreen() }
        composable("settings") { SettingsScreen() }
        composable("search") {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onNoteClick = { id -> navController.navigate("note/$id") }
            )
        }
    }
}
