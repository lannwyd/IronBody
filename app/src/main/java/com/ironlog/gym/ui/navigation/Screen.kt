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
    data object Splits : Screen

    @Serializable
    data class SplitDayDetail(
        val splitDayId: Long,
        val splitId: Long,
        val dayName: String = ""
    ) : Screen

    @Serializable
    data object BodyWeight : Screen

    @Serializable
    data object Timer : Screen

    @Serializable
    data class Session(
        val splitDayId: Long = -1L,
        val splitId: Long = -1L,
        val dayName: String = ""
    ) : Screen

    @Serializable
    data object SessionHistory : Screen
}
