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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noteflow.app.features.notes.presentation.NoteViewModel

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
                Text("Notes: ${notes.size} ✅", color = Color.White, fontSize = 24.sp)
            }
        }
    }
}
