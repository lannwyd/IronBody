package com.ironlog.gym.domain.model

import androidx.annotation.DrawableRes

data class LibraryExerciseItem(
    val name: String,
    val section: MuscleSection,
    @DrawableRes val iconRes: Int
)
