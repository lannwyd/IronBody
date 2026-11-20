package com.ironlog.gym.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object ExerciseList : Screen

    @Serializable
    data class ExerciseEdit(
        val exerciseId: Long? = null,
        val initialSection: String? = null
    ) : Screen

    @Serializable
    data object BodyWeight : Screen
}
