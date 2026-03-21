package com.noteflow.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class NoteFlowApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            File(cacheDir, "crash_log.txt").writeText(throwable.stackTraceToString())
        }
    }
}
