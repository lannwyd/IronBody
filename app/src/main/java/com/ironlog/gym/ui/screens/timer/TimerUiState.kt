package com.ironlog.gym.ui.screens.timer

data class Lap(
    val lapNumber: Int,
    val lapTimeMs: Long,
    val totalTimeMs: Long
)

enum class TimerTab {
    CHRONOMETER,
    COUNTDOWN
}

data class TimerUiState(
    val selectedTab: TimerTab = TimerTab.CHRONOMETER,

    // Chronometer / Stopwatch
    val stopwatchElapsedMs: Long = 0L,
    val isStopwatchRunning: Boolean = false,
    val laps: List<Lap> = emptyList(),

    // Customizable Countdown Timer
    val timerTotalSeconds: Int = 60,
    val timerRemainingMs: Long = 60_000L,
    val isTimerRunning: Boolean = false,
    val isTimerPaused: Boolean = false,
    val isTimerFinished: Boolean = false,

    // Custom time input dialog
    val showCustomTimeDialog: Boolean = false,
    val customMinutes: Int = 1,
    val customSeconds: Int = 30
)
