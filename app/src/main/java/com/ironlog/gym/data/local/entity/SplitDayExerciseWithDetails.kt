package com.ironlog.gym.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.ironlog.gym.domain.model.SplitDayExercise

data class SplitDayExerciseWithDetails(
    @ColumnInfo(name = "split_day_exercise_id")
    val splitDayExerciseId: Long,
    @ColumnInfo(name = "split_day_id")
    val splitDayId: Long,
    @ColumnInfo(name = "day_sort_order")
    val daySortOrder: Int,
    @Embedded val exercise: ExerciseEntity
) {
    fun toDomain(): SplitDayExercise = SplitDayExercise(
        splitDayExerciseId = splitDayExerciseId,
        splitDayId = splitDayId,
        exercise = exercise.toDomain(),
        sortOrder = daySortOrder
    )
}
