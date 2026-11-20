package com.ironlog.gym.domain.model

data class BodyWeight(
    val id: Long = 0,
    val weight: Float,
    val unit: String = "kg",
    val date: Long = System.currentTimeMillis(),
    val receiptImagePath: String? = null,
    val note: String = ""
)
