package com.ironlog.gym.ui.screens.exercises

import com.ironlog.gym.domain.model.Exercise

data class ExerciseListUiState(
    val exercises: List<Exercise> = emptyList(),
    val filteredExercises: List<Exercise> = emptyList(),
    val searchQuery: String = "",
    val selectedSection: String? = null,
    val selectedUnitFilter: String? = null,
    val sectionCounts: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = true,
    val recentlyDeletedExercise: Exercise? = null
)
