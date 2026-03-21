package com.noteflow.app.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFF131313)),
                contentAlignment = Alignment.Center
            ) {
                Text("Home ✅", color = Color.White, fontSize = 24.sp)
            }
        }
        composable("notes") {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFF131313)),
                contentAlignment = Alignment.Center
            ) {
                Text("Notes ✅", color = Color.White, fontSize = 24.sp)
            }
        }
    }
}
