package com.noteflow.app.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.noteflow.app.features.home.presentation.HomeScreen
import com.noteflow.app.features.intro.presentation.IntroScreen
import com.noteflow.app.features.intro.presentation.OnboardingScreen
import com.noteflow.app.features.notes.presentation.screens.NoteDetailScreen
import com.noteflow.app.features.notes.presentation.screens.NoteListScreen
import com.noteflow.app.features.settings.presentation.screens.SettingsScreen
import com.noteflow.app.features.stats.presentation.screens.StatsScreen
import com.noteflow.app.features.tasks.presentation.screens.TaskListScreen
import com.noteflow.app.features.timer.presentation.screens.TimerScreen

private val BgColor = Color(0xFF131313)
private val PrimaryColor = Color(0xFFCABEFF)
private val SurfaceColor = Color(0xFF1C1B1B)

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "الرئيسية", Icons.Default.Home)
    object Notes : BottomNavItem("notes", "ملاحظات", Icons.Default.Notes)
    object Add : BottomNavItem("add", "إضافة", Icons.Default.Add)
    object Tasks : BottomNavItem("tasks", "مهام", Icons.Default.CheckCircle)
    object Stats : BottomNavItem("stats", "إحصائيات", Icons.Default.BarChart)
}

@Composable
fun AppNavigation(
    isFirstTime: Boolean = false,
    onOnboardingFinished: () -> Unit = {}
) {
    val navController = rememberNavController()
    val bottomItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Notes,
        BottomNavItem.Add,
        BottomNavItem.Tasks,
        BottomNavItem.Stats
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomRoutes = listOf("home", "notes", "tasks", "stats")

    Scaffold(
        containerColor = BgColor,
        bottomBar = {
            if (currentRoute in bottomRoutes) {
                NavigationBar(
                    containerColor = SurfaceColor,
                    tonalElevation = 0.dp
                ) {
                    bottomItems.forEach { item ->
                        val isAdd = item.route == "add"
                        NavigationBarItem(
                            icon = {
                                if (isAdd) {
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = PrimaryColor,
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            item.icon,
                                            contentDescription = item.label,
                                            tint = Color(0xFF1C0062),
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                } else {
                                    Icon(item.icon, contentDescription = item.label)
                                }
                            },
                            label = { if (!isAdd) Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                if (item.route == "add") {
                                    navController.navigate("note/0")
                                } else {
                                    navController.navigate(item.route) {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryColor,
                                selectedTextColor = PrimaryColor,
                                unselectedIconColor = Color(0xFF929097),
                                unselectedTextColor = Color(0xFF929097),
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "intro",
            modifier = Modifier.padding(padding)
        ) {
            composable("intro") {
                IntroScreen(
                    onFinished = {
                        if (isFirstTime) {
                            navController.navigate("onboarding") {
                                popUpTo("intro") { inclusive = true }
                            }
                        } else {
                            navController.navigate("home") {
                                popUpTo("intro") { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable("onboarding") {
                OnboardingScreen(
                    onFinished = {
                        onOnboardingFinished()
                        navController.navigate("home") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }
            composable("home") {
                HomeScreen(
                    onNavigateToNotes = { navController.navigate("notes") },
                    onNavigateToStats = { navController.navigate("stats") },
                    onNavigateToSettings = { navController.navigate("settings") },
                    onNoteClick = { id -> navController.navigate("note/$id") },
                    onAddNote = { navController.navigate("note/0") },
                    onNavigateToTimer = { navController.navigate("timer") },
                    onNavigateToTasks = { navController.navigate("tasks") }
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
            composable("timer") { TimerScreen() }
            composable("stats") { StatsScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}
