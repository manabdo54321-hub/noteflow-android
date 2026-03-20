package com.noteflow.app.features.timer.presentation

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteflow.app.features.timer.data.local.SessionEntity
import com.noteflow.app.features.timer.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    companion object {
        const val WORK_DURATION = 25 * 60 * 1000L
        const val BREAK_DURATION = 5 * 60 * 1000L
    }

    private val _timeLeft = MutableStateFlow(WORK_DURATION)
    val timeLeft: StateFlow<Long> = _timeLeft

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _isWorkSession = MutableStateFlow(true)
    val isWorkSession: StateFlow<Boolean> = _isWorkSession

    private val _completedSessions = MutableStateFlow(0)
    val completedSessions: StateFlow<Int> = _completedSessions

    private var countDownTimer: CountDownTimer? = null
    private var sessionStartTime: Long = 0L
    private var currentTaskId: Long? = null

    fun setTask(taskId: Long?) { currentTaskId = taskId }

    fun start() {
        if (_isRunning.value) return
        _isRunning.value = true
        sessionStartTime = System.currentTimeMillis()

        countDownTimer = object : CountDownTimer(_timeLeft.value, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = millisUntilFinished
            }
            override fun onFinish() {
                _isRunning.value = false
                _timeLeft.value = 0
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
        _timeLeft.value = if (_isWorkSession.value) WORK_DURATION else BREAK_DURATION
    }

    private fun onSessionFinished() {
        viewModelScope.launch {
            sessionRepository.saveSession(
                SessionEntity(
                    taskId = currentTaskId,
                    startTime = sessionStartTime,
                    endTime = System.currentTimeMillis(),
                    durationMinutes = if (_isWorkSession.value) 25 else 5,
                    isWorkSession = _isWorkSession.value
                )
            )
        }
        if (_isWorkSession.value) _completedSessions.value++
        _isWorkSession.value = !_isWorkSession.value
        _timeLeft.value = if (_isWorkSession.value) WORK_DURATION else BREAK_DURATION
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
