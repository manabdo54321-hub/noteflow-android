package com.noteflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.noteflow.app.core.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val crashFile = File(cacheDir, "crash_log.txt")
        val previousCrash = if (crashFile.exists()) crashFile.readText() else null
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            crashFile.writeText(throwable.stackTraceToString())
        }
        setContent {
            AppNavigation(isFirstTime = false, onOnboardingFinished = {})
        }
    }
}
