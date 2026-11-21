package com.ironlog.gym.ui.screens.splits.daydetail

import com.ironlog.gym.domain.model.Exercise
import com.ironlog.gym.domain.model.SplitDay
import com.ironlog.gym.domain.model.SplitDayExercise

data class SplitDayDetailUiState(
    val day: SplitDay? = null,
    val splitDayId: Long = 0,
    val splitId: Long = 0,
    val dayName: String = "",
    val dayExercises: List<SplitDayExercise> = emptyList(),
    val allGlobalExercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = true,
    val showExercisePicker: Boolean = false,
    val showRenameDayDialog: Boolean = false,
    val recentlyRemovedExercise: SplitDayExercise? = null
)
