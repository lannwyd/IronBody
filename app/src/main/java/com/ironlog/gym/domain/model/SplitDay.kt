package com.ironlog.gym.domain.model

data class SplitDay(
    val id: Long = 0,
    val splitId: Long,
    val name: String,
    val sortOrder: Int = 0,
    val exerciseCount: Int = 0
)
