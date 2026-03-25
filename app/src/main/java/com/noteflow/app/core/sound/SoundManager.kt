package com.noteflow.app.core.sound

import android.content.Context
import android.media.MediaPlayer
import android.media.AudioAttributes
import android.os.Build
import com.noteflow.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class WhiteNoiseType(val label: String, val emoji: String, val resId: Int) {
    NONE("بدون صوت", "🔇", 0),
    WHITE_NOISE("ضوضاء بيضاء", "🌫️", R.raw.white_noise),
    RAIN("مطر", "🌧️", R.raw.rain_loop),
    WAVES("موج", "🌊", R.raw.wave_loop),
    FIRE("نار", "🔥", R.raw.fire_loop)
}

enum class TimerBellType(val label: String, val resId: Int) {
    DEFAULT("افتراضي", R.raw.bell_classic),
    SOFT("ناعم", R.raw.bell_soft),
    NATURE("طبيعة", R.raw.bell_nature),
    DIGITAL("إلكتروني", R.raw.bell_digital)
}

@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var whiteNoisePlayer: MediaPlayer? = null
    private var bellPlayer: MediaPlayer? = null
    private var fxPlayer: MediaPlayer? = null

    private fun buildPlayer(resId: Int, looping: Boolean = false): MediaPlayer {
        return MediaPlayer().apply {
            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            setAudioAttributes(attrs)
            val afd = context.resources.openRawResourceFd(resId)
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            isLooping = looping
            prepare()
        }
    }

    fun playWhiteNoise(type: WhiteNoiseType) {
        stopWhiteNoise()
        if (type == WhiteNoiseType.NONE) return
        try {
            whiteNoisePlayer = buildPlayer(type.resId, looping = true)
            whiteNoisePlayer?.start()
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun stopWhiteNoise() {
        try {
            whiteNoisePlayer?.stop()
            whiteNoisePlayer?.release()
        } catch (e: Exception) { }
        whiteNoisePlayer = null
    }

    fun playTimerBell(type: TimerBellType = TimerBellType.DEFAULT) {
        try {
            bellPlayer?.release()
            bellPlayer = buildPlayer(type.resId, looping = false)
            bellPlayer?.start()
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun playTaskComplete() = playFx(R.raw.sound_task_complete)
    fun playNoteSaved() = playFx(R.raw.sound_note_saved)
    fun playSessionComplete() = playFx(R.raw.sound_session_complete)
    fun playNotification() = playFx(R.raw.sound_notification)

    private fun playFx(resId: Int) {
        try {
            fxPlayer?.release()
            fxPlayer = buildPlayer(resId, looping = false)
            fxPlayer?.start()
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun release() {
        stopWhiteNoise()
        bellPlayer?.release()
        fxPlayer?.release()
        bellPlayer = null
        fxPlayer = null
    }
}
