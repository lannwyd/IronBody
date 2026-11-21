package com.ironlog.gym.domain.model

data class FamousSplitPreset(
    val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val defaultDays: List<String>
) {
    companion object {
        val ALL: List<FamousSplitPreset> = listOf(
            FamousSplitPreset(
                id = "ppl_3",
                name = "Push Pull Legs (PPL)",
                subtitle = "3 Days / Week",
                description = "Classic hypertrophy routine targeting pushing muscles, pulling muscles, and lower body.",
                defaultDays = listOf(
                    "Push (Chest, Shoulders, Triceps)",
                    "Pull (Back, Biceps, Forearms)",
                    "Legs & Abs"
                )
            ),
            FamousSplitPreset(
                id = "ppl_6",
                name = "Push Pull Legs (6 Days)",
                subtitle = "6 Days / Week",
                description = "High frequency split hitting each major muscle group twice per week with dedicated focus.",
                defaultDays = listOf(
                    "Push A (Chest & Triceps)",
                    "Pull A (Back & Biceps)",
                    "Legs A (Quads & Calves)",
                    "Push B (Shoulders & Chest)",
                    "Pull B (Back & Rear Delts)",
                    "Legs B (Hamstrings & Glutes)"
                )
            ),
            FamousSplitPreset(
                id = "upper_lower",
                name = "Upper / Lower Split",
                subtitle = "4 Days / Week",
                description = "Optimal balance between recovery and training volume, alternating upper and lower sessions.",
                defaultDays = listOf(
                    "Upper Body A",
                    "Lower Body A",
                    "Upper Body B",
                    "Lower Body B"
                )
            ),
            FamousSplitPreset(
                id = "arnold",
                name = "Arnold Split",
                subtitle = "6 Days / Week",
                description = "Golden Era routine matching agonist-antagonist muscle groups for maximum hypertrophy.",
                defaultDays = listOf(
                    "Chest & Back",
                    "Shoulders & Arms",
                    "Legs & Abs"
                )
            ),
            FamousSplitPreset(
                id = "bro_split",
                name = "Bro Split (Body Part)",
                subtitle = "5 Days / Week",
                description = "Traditional 5-day bodybuilding routine annihilating one muscle group each session.",
                defaultDays = listOf(
                    "Monday - Chest",
                    "Tuesday - Back",
                    "Wednesday - Shoulders",
                    "Thursday - Legs",
                    "Friday - Arms (Biceps & Triceps)"
                )
            ),
            FamousSplitPreset(
                id = "full_body",
                name = "Full Body Split",
                subtitle = "3 Days / Week",
                description = "Full body stimulation 3 times a week with ample rest days for complete recovery.",
                defaultDays = listOf(
                    "Full Body A",
                    "Full Body B",
                    "Full Body C"
                )
            ),
            FamousSplitPreset(
                id = "torso_limbs",
                name = "Torso & Limbs",
                subtitle = "4 Days / Week",
                description = "Divides torso (chest, back, shoulders) and limbs (arms, legs) for great fatigue management.",
                defaultDays = listOf(
                    "Torso A (Chest, Back, Shoulders)",
                    "Limbs A (Arms & Legs)",
                    "Torso B (Chest, Back, Shoulders)",
                    "Limbs B (Arms & Legs)"
                )
            ),
            FamousSplitPreset(
                id = "phat",
                name = "Power Hypertrophy (PHAT)",
                subtitle = "5 Days / Week",
                description = "Combines 2 heavy powerlifting days with 3 high-volume hypertrophy bodybuilding days.",
                defaultDays = listOf(
                    "Upper Body Power",
                    "Lower Body Power",
                    "Back & Shoulders Hypertrophy",
                    "Lower Body Hypertrophy",
                    "Chest & Arms Hypertrophy"
                )
            )
        )
    }
}
