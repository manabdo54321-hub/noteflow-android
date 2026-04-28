package com.noteflow.app

import android.content.Context
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val log = "=".repeat(60) + "\nCRASH at: $timestamp\nThread: ${thread.name}\n" + "=".repeat(60) + "\n" + sw.toString() + "\n\n"
            File(context.filesDir, "crash_log.txt").appendText(log)
        } catch (e: Exception) {}
        finally { defaultHandler?.uncaughtException(thread, throwable) }
    }

    companion object {
        fun install(context: Context) {
            Thread.setDefaultUncaughtExceptionHandler(CrashHandler(context))
        }
    }
}
