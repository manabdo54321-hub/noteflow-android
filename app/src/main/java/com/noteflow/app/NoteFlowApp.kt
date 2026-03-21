package com.noteflow.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class NoteFlowApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val crashFile = File("/sdcard/Download/crash_log.txt")
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            crashFile.writeText(throwable.stackTraceToString())
        }
    }
}
