package com.ironlog.gym.domain.model

enum class MuscleSection(
    val id: String,
    val displayName: String
) {
    CHEST("chest", "Chest"),
    BACK("back", "Back"),
    BICEPS("biceps", "Biceps"),
    TRICEPS("triceps", "Triceps"),
    FOREARMS("forearms", "Forearms"),
    SHOULDERS("shoulders", "Shoulders"),
    ABS("abs", "Abs"),
    LEGS("legs", "Legs");

    companion object {
        val ALL = listOf(CHEST, BACK, BICEPS, TRICEPS, FOREARMS, SHOULDERS, ABS, LEGS)

        fun fromId(id: String?): MuscleSection {
            if (id.isNullOrBlank()) return CHEST
            val clean = id.trim().lowercase()
            return entries.firstOrNull {
                it.id.equals(clean, ignoreCase = true) ||
                it.displayName.equals(clean, ignoreCase = true) ||
                (clean == "forarms" && it == FOREARMS)
            } ?: CHEST
        }
    }
}
