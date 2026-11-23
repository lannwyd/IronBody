package com.ironlog.gym.ui.screens.timer

import android.content.Context
import android.os.SystemClock
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

data class ChronometerState(
    val elapsedMs: Long = 0L,
    val isRunning: Boolean = false,
    val laps: List<Lap> = emptyList()
)

@Singleton
class ChronometerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(ChronometerState())
    val state: StateFlow<ChronometerState> = _state.asStateFlow()

    private var tickerJob: Job? = null
    private var baseTime: Long = 0L

    fun start() {
        if (_state.value.isRunning) return
        baseTime = SystemClock.elapsedRealtime() - _state.value.elapsedMs
        _state.update { it.copy(isRunning = true) }

        TimerNotificationHelper.showChronometerNotification(
            context = context,
            isRunning = true,
            elapsedMs = _state.value.elapsedMs,
            lapsCount = _state.value.laps.size
        )

        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (isActive) {
                val elapsed = SystemClock.elapsedRealtime() - baseTime
                _state.update { it.copy(elapsedMs = elapsed) }
                delay(25)
            }
        }
    }

    fun pause() {
        tickerJob?.cancel()
        tickerJob = null
        _state.update { it.copy(isRunning = false) }

        TimerNotificationHelper.showChronometerNotification(
            context = context,
            isRunning = false,
            elapsedMs = _state.value.elapsedMs,
            lapsCount = _state.value.laps.size
        )
    }

    fun lap() {
        val currentElapsed = _state.value.elapsedMs
        if (currentElapsed == 0L) return
        val laps = _state.value.laps
        val previousTotal = laps.firstOrNull()?.totalTimeMs ?: 0L
        val lapTime = currentElapsed - previousTotal
        val newLap = Lap(
            lapNumber = laps.size + 1,
            lapTimeMs = lapTime,
            totalTimeMs = currentElapsed
        )
        _state.update { it.copy(laps = listOf(newLap) + laps) }

        if (_state.value.isRunning) {
            TimerNotificationHelper.showChronometerNotification(
                context = context,
                isRunning = true,
                elapsedMs = _state.value.elapsedMs,
                lapsCount = _state.value.laps.size
            )
        }
    }

    fun reset() {
        tickerJob?.cancel()
        tickerJob = null
        _state.update {
            it.copy(
                isRunning = false,
                elapsedMs = 0L,
                laps = emptyList()
            )
        }
        TimerNotificationHelper.cancelChronometerNotification(context)
    }
}
