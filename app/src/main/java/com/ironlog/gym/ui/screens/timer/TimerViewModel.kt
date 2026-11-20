package com.ironlog.gym.ui.screens.timer

import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class TimerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var stopwatchJob: Job? = null
    private var stopwatchBaseTime: Long = 0L

    private var timerJob: Job? = null
    private var timerTargetEndTime: Long = 0L

    fun selectTab(tab: TimerTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    // ──────────────────────────────────────────────
    // CHRONOMETER (STOPWATCH)
    // ──────────────────────────────────────────────

    fun startStopwatch() {
        if (_uiState.value.isStopwatchRunning) return

        stopwatchBaseTime = SystemClock.elapsedRealtime() - _uiState.value.stopwatchElapsedMs
        _uiState.update { it.copy(isStopwatchRunning = true) }

        stopwatchJob = viewModelScope.launch {
            while (isActive) {
                val elapsed = SystemClock.elapsedRealtime() - stopwatchBaseTime
                _uiState.update { it.copy(stopwatchElapsedMs = elapsed) }
                delay(25) // ~40 FPS updates for ultra-smooth hundredths display
            }
        }
    }

    fun pauseStopwatch() {
        stopwatchJob?.cancel()
        stopwatchJob = null
        _uiState.update { it.copy(isStopwatchRunning = false) }
    }

    fun resetStopwatch() {
        stopwatchJob?.cancel()
        stopwatchJob = null
        _uiState.update {
            it.copy(
                isStopwatchRunning = false,
                stopwatchElapsedMs = 0L,
                laps = emptyList()
            )
        }
    }

    fun recordLap() {
        val currentElapsed = _uiState.value.stopwatchElapsedMs
        if (currentElapsed == 0L) return

        val laps = _uiState.value.laps
        val previousTotal = laps.firstOrNull()?.totalTimeMs ?: 0L
        val lapTime = currentElapsed - previousTotal

        val newLap = Lap(
            lapNumber = laps.size + 1,
            lapTimeMs = lapTime,
            totalTimeMs = currentElapsed
        )

        _uiState.update {
            it.copy(laps = listOf(newLap) + laps)
        }
    }

    // ──────────────────────────────────────────────
    // CUSTOMIZABLE COUNTDOWN TIMER
    // ──────────────────────────────────────────────

    fun setPresetDuration(seconds: Int) {
        if (_uiState.value.isTimerRunning) {
            cancelTimer()
        }
        val safeSeconds = max(5, seconds)
        _uiState.update {
            it.copy(
                timerTotalSeconds = safeSeconds,
                timerRemainingMs = safeSeconds * 1000L,
                isTimerRunning = false,
                isTimerPaused = false,
                isTimerFinished = false
            )
        }
    }

    fun adjustTimerSeconds(deltaSeconds: Int) {
        val currentRemainingSeconds = (_uiState.value.timerRemainingMs / 1000).toInt()
        val newRemainingSeconds = max(5, currentRemainingSeconds + deltaSeconds)
        val newTotalSeconds = max(_uiState.value.timerTotalSeconds, newRemainingSeconds)

        if (_uiState.value.isTimerRunning) {
            timerTargetEndTime = SystemClock.elapsedRealtime() + (newRemainingSeconds * 1000L)
            TimerNotificationHelper.cancelAlarm(context)
            TimerNotificationHelper.scheduleAlarm(
                context,
                System.currentTimeMillis() + (newRemainingSeconds * 1000L)
            )
        }

        _uiState.update {
            it.copy(
                timerTotalSeconds = newTotalSeconds,
                timerRemainingMs = newRemainingSeconds * 1000L,
                isTimerFinished = false
            )
        }
    }

    fun startTimer() {
        if (_uiState.value.isTimerRunning) return

        val remainingMs = _uiState.value.timerRemainingMs
        if (remainingMs <= 0L) return

        timerTargetEndTime = SystemClock.elapsedRealtime() + remainingMs
        TimerNotificationHelper.scheduleAlarm(context, System.currentTimeMillis() + remainingMs)

        _uiState.update {
            it.copy(
                isTimerRunning = true,
                isTimerPaused = false,
                isTimerFinished = false
            )
        }

        timerJob = viewModelScope.launch {
            while (isActive) {
                val remaining = timerTargetEndTime - SystemClock.elapsedRealtime()
                if (remaining <= 0L) {
                    onTimerFinished()
                    break
                }
                _uiState.update { it.copy(timerRemainingMs = remaining) }
                delay(50)
            }
        }
    }

    fun pauseTimer() {
        cancelTimer()
        _uiState.update {
            it.copy(
                isTimerRunning = false,
                isTimerPaused = true
            )
        }
    }

    fun resetTimer() {
        cancelTimer()
        _uiState.update {
            it.copy(
                timerRemainingMs = it.timerTotalSeconds * 1000L,
                isTimerRunning = false,
                isTimerPaused = false,
                isTimerFinished = false
            )
        }
    }

    fun restartTimer() {
        resetTimer()
        startTimer()
    }

    private fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        TimerNotificationHelper.cancelAlarm(context)
    }

    private fun onTimerFinished() {
        cancelTimer()
        _uiState.update {
            it.copy(
                timerRemainingMs = 0L,
                isTimerRunning = false,
                isTimerPaused = false,
                isTimerFinished = true
            )
        }

        // Trigger in-app sound and vibration
        playAlarmFeedback()

        // Trigger system notification
        TimerNotificationHelper.showTimerFinishedNotification(context)
    }

    private fun playAlarmFeedback() {
        try {
            // Vibration
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400, 200, 400), -1))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(longArrayOf(0, 400, 200, 400, 200, 400), -1)
                }
            }

            // Sound
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, notificationUri)
            ringtone?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ──────────────────────────────────────────────
    // CUSTOM TIME DIALOG
    // ──────────────────────────────────────────────

    fun openCustomTimeDialog() {
        val totalSec = _uiState.value.timerTotalSeconds
        _uiState.update {
            it.copy(
                showCustomTimeDialog = true,
                customMinutes = totalSec / 60,
                customSeconds = totalSec % 60
            )
        }
    }

    fun dismissCustomTimeDialog() {
        _uiState.update { it.copy(showCustomTimeDialog = false) }
    }

    fun applyCustomTime(minutes: Int, seconds: Int) {
        val totalSec = max(5, (minutes * 60) + seconds)
        setPresetDuration(totalSec)
        dismissCustomTimeDialog()
    }

    override fun onCleared() {
        super.onCleared()
        stopwatchJob?.cancel()
        timerJob?.cancel()
    }
}
