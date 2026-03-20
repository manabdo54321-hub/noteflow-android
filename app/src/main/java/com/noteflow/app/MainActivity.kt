package com.noteflow.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.noteflow.app.core.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("noteflow_prefs", Context.MODE_PRIVATE)
        val isFirstTime = prefs.getBoolean("is_first_time", true)

        // لو مش أول مرة، احفظ إن الـ intro اتشافت
        if (!isFirstTime) {
            prefs.edit().putBoolean("intro_seen", true).apply()
        }

        setContent {
            MaterialTheme {
                Surface {
                    AppNavigation(
                        isFirstTime = isFirstTime,
                        onOnboardingFinished = {
                            prefs.edit()
                                .putBoolean("is_first_time", false)
                                .putBoolean("intro_seen", true)
                                .apply()
                        }
                    )
                }
            }
        }
    }
}
