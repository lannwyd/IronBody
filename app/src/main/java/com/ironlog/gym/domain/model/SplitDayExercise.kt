package com.ironlog.gym.domain.model

data class SplitDayExercise(
    val splitDayExerciseId: Long = 0,
    val splitDayId: Long,
    val exercise: Exercise,
    val sortOrder: Int = 0
)
