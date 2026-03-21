package com.noteflow.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.noteflow.app.core.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("noteflow_prefs", Context.MODE_PRIVATE)
        val isFirstTime = prefs.getBoolean("is_first_time", true)
        setContent {
            var error by remember { mutableStateOf<String?>(null) }
            MaterialTheme {
                Surface {
                    if (error != null) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color(0xFF131313)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(error!!, color = Color.Red, fontSize = 14.sp)
                        }
                    } else {
                        try {
                            AppNavigation(
                                isFirstTime = isFirstTime,
                                onOnboardingFinished = {
                                    prefs.edit().putBoolean("is_first_time", false).apply()
                                }
                            )
                        } catch (e: Exception) {
                            error = e.message ?: "خطأ غير معروف"
                        }
                    }
                }
            }
        }
    }
}
