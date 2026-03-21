package com.noteflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noteflow.app.core.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crashFile = File(cacheDir, "crash_log.txt")
        val previousCrash = if (crashFile.exists()) crashFile.readText() else null
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            crashFile.writeText(throwable.stackTraceToString())
        }
        setContent {
            if (previousCrash != null) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp)
                ) {
                    Text(
                        text = previousCrash,
                        color = Color.Red,
                        fontSize = 10.sp,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            } else {
                MaterialTheme {
                    Surface {
                        AppNavigation(isFirstTime = false, onOnboardingFinished = {})
                    }
                }
            }
        }
    }
}
