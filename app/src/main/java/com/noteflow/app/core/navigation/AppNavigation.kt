package com.noteflow.app.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.noteflow.app.features.intro.presentation.IntroScreen
import com.noteflow.app.features.notes.presentation.screens.NoteDetailScreen
import com.noteflow.app.features.notes.presentation.screens.NoteListScreen
import com.noteflow.app.features.settings.presentation.screens.SettingsScreen
import com.noteflow.app.features.stats.presentation.screens.StatsScreen
import com.noteflow.app.features.tasks.presentation.screens.TaskListScreen
import com.noteflow.app.features.timer.presentation.screens.TimerScreen

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Notes : BottomNavItem("notes", "ملاحظات", Icons.Default.Home)
    object Tasks : BottomNavItem("tasks", "مهام", Icons.Default.CheckCircle)
    object Timer : BottomNavItem("timer", "تايمر", Icons.Default.Timer)
    object Stats : BottomNavItem("stats", "إحصائيات", Icons.Default.BarChart)
    object Settings : BottomNavItem("settings", "إعدادات", Icons.Default.Settings)
}

@Composable
fun AppNavigation(
    isFirstTime: Boolean = false,
    onOnboardingFinished: () -> Unit = {}
) {
    val navController = rememberNavController()
    val bottomItems = listOf(
        BottomNavItem.Notes,
        BottomNavItem.Tasks,
        BottomNavItem.Timer,
        BottomNavItem.Stats,
        BottomNavItem.Settings
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomRoutes = bottomItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomRoutes) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo("notes") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (isFirstTime) "intro" else "notes",
            modifier = Modifier.padding(padding)
        ) {
            composable("intro") {
                IntroScreen(
                    onFinished = {
                        onOnboardingFinished()
                        navController.navigate("notes") {
                            popUpTo("intro") { inclusive = true }
                        }
                    }
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
                    onBack = { navController.popBackStack() },
                    onNavigateToNote = { targetId ->
                        navController.navigate("note/$targetId")
                    }
                )
            }
            composable("tasks") {
                TaskListScreen(
                    onNavigateToNote = { id -> navController.navigate("note/$id") }
                )
            }
            composable("timer") {
                TimerScreen()
            }
            composable("stats") {
                StatsScreen()
            }
            composable("settings") {
                SettingsScreen()
            }
        }
    }
}
