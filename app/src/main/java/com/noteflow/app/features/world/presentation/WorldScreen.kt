package com.noteflow.app.features.world.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BgColor = Color(0xFF131313)
private val PrimaryColor = Color(0xFFCABEFF)
private val SurfaceColor = Color(0xFF1C1B1B)
private val TextSecondary = Color(0xFF999999)
private val AccentColor = Color(0xFF8A70FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(BgColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Forest,
                contentDescription = null,
                tint = AccentColor,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("عالم البناء", fontSize = 24.sp,
                fontWeight = FontWeight.Bold, color = PrimaryColor)
            Spacer(modifier = Modifier.height(8.dp))
            Text("قريباً — شجرة حياتك ستنمو هنا",
                fontSize = 14.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(24.dp))
            LinearProgressIndicator(
                progress = 0.1f,
                modifier = Modifier.width(200.dp),
                color = AccentColor,
                trackColor = SurfaceColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("المستوى 1 — بذرة", fontSize = 12.sp, color = TextSecondary)
        }
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack,
                        contentDescription = null, tint = PrimaryColor)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}
