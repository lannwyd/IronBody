package com.ironlog.gym.ui.screens.exerciseEdit

data class ExerciseEditUiState(
    val exerciseId: Long? = null,
    val name: String = "",
    val nameError: Int? = null,
    val weight: String = "",
    val weightError: Int? = null,
    val unit: String = "kg",
    val section: String = "chest",
    val imagePath: String? = null,
    val note: String = "",
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)
