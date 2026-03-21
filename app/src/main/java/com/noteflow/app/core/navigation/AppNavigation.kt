package com.noteflow.app.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun AppNavigation(
    isFirstTime: Boolean = false,
    onOnboardingFinished: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF131313)),
        contentAlignment = Alignment.Center
    ) {
        Text("الشاشة الرئيسية ✅", color = Color.White, fontSize = 24.sp)
    }
}
