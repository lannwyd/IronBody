package com.ironlog.gym.domain.model

data class SessionExerciseLog(
    val id: Long = 0,
    val sessionId: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val exerciseWeight: Float,
    val exerciseUnit: String,
    val sortOrder: Int,
    val chronoDurationMs: Long = 0,
    val isCompleted: Boolean = false
)
