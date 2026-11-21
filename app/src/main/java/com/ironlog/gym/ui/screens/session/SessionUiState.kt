package com.ironlog.gym.ui.screens.session

data class ExerciseTimerState(
    val logId: Long = 0,
    val exerciseId: Long,
    val exerciseName: String,
    val exerciseWeight: Float,
    val exerciseUnit: String,
    val sortOrder: Int,
    val chronoElapsedMs: Long = 0,
    val isChronoRunning: Boolean = false,
    val isCompleted: Boolean = false,
    // Per-exercise rest timer countdown
    val restTimerTotalSeconds: Int = 90,
    val restTimerRemainingMs: Long = 0,
    val isRestTimerRunning: Boolean = false,
    val isRestTimerFinished: Boolean = false
)

data class SessionUiState(
    val isLoading: Boolean = true,
    val dayName: String = "",
    val sessionId: Long = 0,

    // Session chrono (counts up for the entire workout)
    val sessionElapsedMs: Long = 0,
    val isSessionRunning: Boolean = false,

    // Session timer (optional countdown target for overall workout)
    val sessionTimerTotalSeconds: Int? = null,
    val sessionTimerRemainingMs: Long = 0,
    val isSessionTimerRunning: Boolean = false,
    val isSessionTimerFinished: Boolean = false,
    val showSessionTimerDialog: Boolean = false,

    // Exercise list with individual chronos and rest timers
    val exercises: List<ExerciseTimerState> = emptyList(),
    val activeExerciseIndex: Int = -1,

    // Bluetooth earbud control
    val isBluetoothControlEnabled: Boolean = false,

    // Finish session dialog & state
    val showFinishDialog: Boolean = false,
    val isSessionFinished: Boolean = false
)
