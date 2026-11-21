package com.ironlog.gym.service

import com.ironlog.gym.ui.screens.session.ExerciseTimerState

data class WorkoutSessionState(
    val sessionId: Long = 0,
    val splitId: Long = 0,
    val splitDayId: Long = 0,
    val dayName: String = "",

    // Total session chronometer
    val sessionElapsedMs: Long = 0,
    val isSessionRunning: Boolean = false,

    // Optional overall session countdown timer
    val sessionTimerTotalSeconds: Int? = null,
    val sessionTimerRemainingMs: Long = 0,
    val isSessionTimerRunning: Boolean = false,
    val isSessionTimerFinished: Boolean = false,

    // Exercise list and active exercise
    val exercises: List<ExerciseTimerState> = emptyList(),
    val activeExerciseIndex: Int = -1
) {
    val activeExercise: ExerciseTimerState?
        get() = exercises.getOrNull(activeExerciseIndex)

    val activeRestExercise: ExerciseTimerState?
        get() = exercises.firstOrNull { it.isRestTimerRunning || it.isRestTimerFinished }
}
