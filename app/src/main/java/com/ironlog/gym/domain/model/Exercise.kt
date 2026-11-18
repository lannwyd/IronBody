package com.ironlog.gym.domain.model

data class Exercise(
    val id: Long = 0,
    val name: String,
    val weight: Float,
    val unit: String = DEFAULT_UNIT,
    val imagePath: String? = null,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val note: String = "",
    val section: String = MuscleSection.CHEST.id
) {
    companion object {
        const val DEFAULT_UNIT = "kg"
        const val UNIT_KG = "kg"
        const val UNIT_LBS = "lbs"
    }
}
