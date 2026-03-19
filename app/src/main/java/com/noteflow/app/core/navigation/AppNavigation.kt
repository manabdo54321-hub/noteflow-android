package com.noteflow.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.noteflow.app.features.notes.presentation.screens.NoteDetailScreen
import com.noteflow.app.features.notes.presentation.screens.NoteListScreen
import com.noteflow.app.features.tasks.presentation.screens.TaskListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "notes") {
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
                onBack = { navController.popBackStack() }
            )
        }
        composable("tasks") {
            TaskListScreen()
        }
    }
}
