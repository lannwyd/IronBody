package com.ironlog.gym.domain.model

data class WorkoutSession(
    val id: Long = 0,
    val splitId: Long,
    val splitDayId: Long,
    val splitDayName: String = "",
    val startTime: Long = 0,
    val endTime: Long? = null,
    val totalDurationMs: Long = 0,
    val date: String = ""
)
