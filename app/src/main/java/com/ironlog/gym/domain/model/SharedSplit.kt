package com.ironlog.gym.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SharedExercise(
    val name: String,
    val weight: Float,
    val unit: String = "kg",
    val section: String = "chest",
    val note: String = "",
    val sortOrder: Int = 0
)

@Serializable
data class SharedSplitDay(
    val name: String,
    val sortOrder: Int = 0,
    val exercises: List<SharedExercise> = emptyList()
)

@Serializable
data class SharedSplit(
    val version: Int = 1,
    val app: String = "IronBody",
    val name: String,
    val description: String = "",
    val days: List<SharedSplitDay> = emptyList()
)
