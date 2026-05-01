package com.noteflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.*
import com.noteflow.app.core.navigation.AppNavigation
import com.noteflow.app.ui.screens.CrashLogScreen
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        CrashHandler.install(applicationContext)
        setContent {
            val hasCrash = remember { File(cacheDir, "crash_log.txt").exists() }
            if (hasCrash) {
                CrashLogScreen()
            } else {
                AppNavigation(isFirstTime = false, onOnboardingFinished = {})
            }
        }
    }
}
