package com.noteflow.app.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

@Composable
fun CrashLogScreen() {
    val context = LocalContext.current
    val crashFile = File(context.cacheDir, "crash_log.txt")
    val crashLog = remember { if (crashFile.exists()) crashFile.readText() else "" }

    if (crashLog.isBlank()) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF131313))
            .padding(16.dp)
    ) {
        Text("آخر كراش:", color = Color(0xFFFF6B6B), fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF1C1B1B))
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(crashLog, color = Color.White, fontSize = 11.sp, lineHeight = 16.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                crashFile.delete()
                (context as? Activity)?.recreate()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A70FF))
        ) {
            Text("امسح وكمل", color = Color.White)
        }
    }
}
