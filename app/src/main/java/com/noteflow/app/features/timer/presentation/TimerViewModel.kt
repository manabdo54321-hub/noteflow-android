package com.noteflow.app.features.timer.presentation

import android.content.Context
import android.media.RingtoneManager
import com.noteflow.app.core.sound.SoundManager
import com.noteflow.app.core.sound.TimerBellType
import com.noteflow.app.core.sound.WhiteNoiseType
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteflow.app.features.timer.data.local.SessionEntity
import com.noteflow.app.features.timer.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val soundManager: SoundManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        const val WORK_DURATION = 25 * 60 * 1000L
        const val BREAK_DURATION = 5 * 60 * 1000L
        const val LONG_BREAK_DURATION = 15 * 60 * 1000L
    }

    private val _timeLeft = MutableStateFlow(WORK_DURATION)
    val timeLeft: StateFlow<Long> = _timeLeft

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _isWorkSession = MutableStateFlow(true)
    val isWorkSession: StateFlow<Boolean> = _isWorkSession

    private val _completedSessions = MutableStateFlow(0)
    val completedSessions: StateFlow<Int> = _completedSessions

    private val _sessionFinished = MutableStateFlow(false)
    val sessionFinished: StateFlow<Boolean> = _sessionFinished

    private val _customDuration = MutableStateFlow(WORK_DURATION)
    val customDuration: StateFlow<Long> = _customDuration

    private var countDownTimer: CountDownTimer? = null
    private var sessionStartTime: Long = 0L
    private var currentTaskId: Long? = null

    fun setTask(taskId: Long?) { currentTaskId = taskId }

    private val _bellType = MutableStateFlow(TimerBellType.DEFAULT)
    val bellType: StateFlow<TimerBellType> = _bellType
    private val _whiteNoiseType = MutableStateFlow(WhiteNoiseType.NONE)
    val whiteNoiseType: StateFlow<WhiteNoiseType> = _whiteNoiseType
    fun setBellType(type: TimerBellType) { _bellType.value = type }
    fun setWhiteNoise(type: WhiteNoiseType) { _whiteNoiseType.value = type; soundManager.playWhiteNoise(type) }
    fun stopWhiteNoise() { soundManager.stopWhiteNoise() }

    fun setCustomDuration(hours: Int, minutes: Int) {
        val duration = (hours * 3600L + minutes * 60L) * 1000L
        if (duration > 0) {
            _customDuration.value = duration
            _timeLeft.value = duration
        }
    }

    fun start() {
        if (_isRunning.value) return
        _isRunning.value = true
        _sessionFinished.value = false
        sessionStartTime = System.currentTimeMillis()
        countDownTimer = object : CountDownTimer(_timeLeft.value, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = millisUntilFinished
            }
            override fun onFinish() {
                _isRunning.value = false
                _timeLeft.value = 0
                _sessionFinished.value = true
                playAlarmAndVibrate()
                onSessionFinished()
            }
        }.start()
    }

    fun pause() {
        countDownTimer?.cancel()
        _isRunning.value = false
    }

    fun reset() {
        countDownTimer?.cancel()
        _isRunning.value = false
        _sessionFinished.value = false
        val isLongBreak = _completedSessions.value % 4 == 0 && _completedSessions.value > 0
        _timeLeft.value = if (_isWorkSession.value) _customDuration.value
            else if (isLongBreak) LONG_BREAK_DURATION else BREAK_DURATION
    }

    fun skipSession() {
        countDownTimer?.cancel()
        _isRunning.value = false
        _sessionFinished.value = false
        if (_isWorkSession.value) _completedSessions.value++
        _isWorkSession.value = !_isWorkSession.value
        val isLongBreak = _completedSessions.value % 4 == 0 && _completedSessions.value > 0
        _timeLeft.value = if (_isWorkSession.value) _customDuration.value
            else if (isLongBreak) LONG_BREAK_DURATION else BREAK_DURATION
    }

    fun acknowledgeFinished() {
        _sessionFinished.value = false
    }

    private fun playAlarmAndVibrate() {
        viewModelScope.launch {
            try { soundManager.playTimerBell(_bellType.value) } catch (e: Exception) { }
            try {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500), -1))
            } catch (e: Exception) { }
        }
    }
    private fun onSessionFinished() {
        viewModelScope.launch {
            sessionRepository.saveSession(
                SessionEntity(
                    taskId = currentTaskId,
                    startTime = sessionStartTime,
                    endTime = System.currentTimeMillis(),
                    durationMinutes = if (_isWorkSession.value) (_customDuration.value / 60000).toInt() else 5,
                    isWorkSession = _isWorkSession.value
                )
            )
        }
        if (_isWorkSession.value) _completedSessions.value++
        _isWorkSession.value = !_isWorkSession.value
        val isLongBreak = _completedSessions.value % 4 == 0 && _completedSessions.value > 0
        _timeLeft.value = if (_isWorkSession.value) _customDuration.value
            else if (isLongBreak) LONG_BREAK_DURATION else BREAK_DURATION
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
