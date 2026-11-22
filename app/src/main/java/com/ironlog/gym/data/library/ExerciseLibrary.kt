package com.ironlog.gym.data.library

import com.ironlog.gym.R
import com.ironlog.gym.domain.model.LibraryExerciseItem
import com.ironlog.gym.domain.model.MuscleSection

object ExerciseLibrary {

    val all: List<LibraryExerciseItem> = listOf(
        // ── CHEST ──────────────────────────────────────────────
        LibraryExerciseItem("Bench Press",              MuscleSection.CHEST,     R.drawable.ic_muscle_chest),
        LibraryExerciseItem("Incline Bench Press",      MuscleSection.CHEST,     R.drawable.ic_muscle_chest),
        LibraryExerciseItem("Decline Bench Press",      MuscleSection.CHEST,     R.drawable.ic_muscle_chest),
        LibraryExerciseItem("Dumbbell Fly",             MuscleSection.CHEST,     R.drawable.ic_muscle_chest),
        LibraryExerciseItem("Incline Dumbbell Press",   MuscleSection.CHEST,     R.drawable.ic_muscle_chest),
        LibraryExerciseItem("Decline Dumbbell Press",   MuscleSection.CHEST,     R.drawable.ic_muscle_chest),
        LibraryExerciseItem("Cable Crossover",          MuscleSection.CHEST,     R.drawable.ic_muscle_chest),
        LibraryExerciseItem("Low Cable Fly",            MuscleSection.CHEST,     R.drawable.ic_muscle_chest),
        LibraryExerciseItem("Pec Deck Machine",         MuscleSection.CHEST,     R.drawable.ic_muscle_chest),
        LibraryExerciseItem("Push-Up",                  MuscleSection.CHEST,     R.drawable.ic_muscle_chest),
        LibraryExerciseItem("Wide Push-Up",             MuscleSection.CHEST,     R.drawable.ic_muscle_chest),
        LibraryExerciseItem("Chest Dips",               MuscleSection.CHEST,     R.drawable.ic_muscle_chest),
        LibraryExerciseItem("Smith Machine Bench Press",MuscleSection.CHEST,     R.drawable.ic_muscle_chest),
        LibraryExerciseItem("Chest Press Machine",      MuscleSection.CHEST,     R.drawable.ic_muscle_chest),

        // ── BACK ───────────────────────────────────────────────
        LibraryExerciseItem("Deadlift",                 MuscleSection.BACK,      R.drawable.ic_muscle_back),
        LibraryExerciseItem("Pull-Up",                  MuscleSection.BACK,      R.drawable.ic_muscle_back),
        LibraryExerciseItem("Chin-Up",                  MuscleSection.BACK,      R.drawable.ic_muscle_back),
        LibraryExerciseItem("Lat Pulldown",             MuscleSection.BACK,      R.drawable.ic_muscle_back),
        LibraryExerciseItem("Wide-Grip Lat Pulldown",   MuscleSection.BACK,      R.drawable.ic_muscle_back),
        LibraryExerciseItem("Seated Cable Row",         MuscleSection.BACK,      R.drawable.ic_muscle_back),
        LibraryExerciseItem("Bent-Over Barbell Row",    MuscleSection.BACK,      R.drawable.ic_muscle_back),
        LibraryExerciseItem("Single-Arm Dumbbell Row",  MuscleSection.BACK,      R.drawable.ic_muscle_back),
        LibraryExerciseItem("T-Bar Row",                MuscleSection.BACK,      R.drawable.ic_muscle_back),
        LibraryExerciseItem("Pendlay Row",              MuscleSection.BACK,      R.drawable.ic_muscle_back),
        LibraryExerciseItem("Face Pull",                MuscleSection.BACK,      R.drawable.ic_muscle_back),
        LibraryExerciseItem("Hyperextension",           MuscleSection.BACK,      R.drawable.ic_muscle_back),
        LibraryExerciseItem("Good Morning",             MuscleSection.BACK,      R.drawable.ic_muscle_back),
        LibraryExerciseItem("Cable Row (High)",         MuscleSection.BACK,      R.drawable.ic_muscle_back),
        LibraryExerciseItem("Meadows Row",              MuscleSection.BACK,      R.drawable.ic_muscle_back),

        // ── BICEPS ─────────────────────────────────────────────
        LibraryExerciseItem("Barbell Curl",             MuscleSection.BICEPS,    R.drawable.ic_muscle_biceps),
        LibraryExerciseItem("Dumbbell Curl",            MuscleSection.BICEPS,    R.drawable.ic_muscle_biceps),
        LibraryExerciseItem("Hammer Curl",              MuscleSection.BICEPS,    R.drawable.ic_muscle_biceps),
        LibraryExerciseItem("Preacher Curl",            MuscleSection.BICEPS,    R.drawable.ic_muscle_biceps),
        LibraryExerciseItem("Concentration Curl",       MuscleSection.BICEPS,    R.drawable.ic_muscle_biceps),
        LibraryExerciseItem("Cable Curl",               MuscleSection.BICEPS,    R.drawable.ic_muscle_biceps),
        LibraryExerciseItem("Incline Dumbbell Curl",    MuscleSection.BICEPS,    R.drawable.ic_muscle_biceps),
        LibraryExerciseItem("Spider Curl",              MuscleSection.BICEPS,    R.drawable.ic_muscle_biceps),
        LibraryExerciseItem("Zottman Curl",             MuscleSection.BICEPS,    R.drawable.ic_muscle_biceps),
        LibraryExerciseItem("Cross-Body Hammer Curl",   MuscleSection.BICEPS,    R.drawable.ic_muscle_biceps),
        LibraryExerciseItem("EZ-Bar Curl",              MuscleSection.BICEPS,    R.drawable.ic_muscle_biceps),

        // ── TRICEPS ────────────────────────────────────────────
        LibraryExerciseItem("Tricep Pushdown",          MuscleSection.TRICEPS,   R.drawable.ic_muscle_triceps),
        LibraryExerciseItem("Skull Crusher",            MuscleSection.TRICEPS,   R.drawable.ic_muscle_triceps),
        LibraryExerciseItem("Overhead Tricep Extension",MuscleSection.TRICEPS,   R.drawable.ic_muscle_triceps),
        LibraryExerciseItem("Close-Grip Bench Press",   MuscleSection.TRICEPS,   R.drawable.ic_muscle_triceps),
        LibraryExerciseItem("Diamond Push-Up",          MuscleSection.TRICEPS,   R.drawable.ic_muscle_triceps),
        LibraryExerciseItem("Tricep Dips",              MuscleSection.TRICEPS,   R.drawable.ic_muscle_triceps),
        LibraryExerciseItem("Tricep Kickback",          MuscleSection.TRICEPS,   R.drawable.ic_muscle_triceps),
        LibraryExerciseItem("Cable Overhead Extension", MuscleSection.TRICEPS,   R.drawable.ic_muscle_triceps),
        LibraryExerciseItem("JM Press",                 MuscleSection.TRICEPS,   R.drawable.ic_muscle_triceps),
        LibraryExerciseItem("Tate Press",               MuscleSection.TRICEPS,   R.drawable.ic_muscle_triceps),

        // ── FOREARMS ───────────────────────────────────────────
        LibraryExerciseItem("Wrist Curl",               MuscleSection.FOREARMS,  R.drawable.ic_muscle_forearms),
        LibraryExerciseItem("Reverse Wrist Curl",       MuscleSection.FOREARMS,  R.drawable.ic_muscle_forearms),
        LibraryExerciseItem("Reverse Curl",             MuscleSection.FOREARMS,  R.drawable.ic_muscle_forearms),
        LibraryExerciseItem("Farmer's Carry",           MuscleSection.FOREARMS,  R.drawable.ic_muscle_forearms),
        LibraryExerciseItem("Plate Pinch",              MuscleSection.FOREARMS,  R.drawable.ic_muscle_forearms),
        LibraryExerciseItem("Dead Hang",                MuscleSection.FOREARMS,  R.drawable.ic_muscle_forearms),
        LibraryExerciseItem("Gripper",                  MuscleSection.FOREARMS,  R.drawable.ic_muscle_forearms),

        // ── SHOULDERS ──────────────────────────────────────────
        LibraryExerciseItem("Overhead Press",           MuscleSection.SHOULDERS, R.drawable.ic_muscle_shoulders),
        LibraryExerciseItem("Arnold Press",             MuscleSection.SHOULDERS, R.drawable.ic_muscle_shoulders),
        LibraryExerciseItem("Lateral Raise",            MuscleSection.SHOULDERS, R.drawable.ic_muscle_shoulders),
        LibraryExerciseItem("Front Raise",              MuscleSection.SHOULDERS, R.drawable.ic_muscle_shoulders),
        LibraryExerciseItem("Rear Delt Fly",            MuscleSection.SHOULDERS, R.drawable.ic_muscle_shoulders),
        LibraryExerciseItem("Cable Lateral Raise",      MuscleSection.SHOULDERS, R.drawable.ic_muscle_shoulders),
        LibraryExerciseItem("Upright Row",              MuscleSection.SHOULDERS, R.drawable.ic_muscle_shoulders),
        LibraryExerciseItem("Machine Shoulder Press",   MuscleSection.SHOULDERS, R.drawable.ic_muscle_shoulders),
        LibraryExerciseItem("Seated Dumbbell Press",    MuscleSection.SHOULDERS, R.drawable.ic_muscle_shoulders),
        LibraryExerciseItem("Push Press",               MuscleSection.SHOULDERS, R.drawable.ic_muscle_shoulders),
        LibraryExerciseItem("Behind-the-Neck Press",    MuscleSection.SHOULDERS, R.drawable.ic_muscle_shoulders),
        LibraryExerciseItem("Shrugs",                   MuscleSection.SHOULDERS, R.drawable.ic_muscle_shoulders),

        // ── ABS ────────────────────────────────────────────────
        LibraryExerciseItem("Crunch",                   MuscleSection.ABS,       R.drawable.ic_muscle_abs),
        LibraryExerciseItem("Plank",                    MuscleSection.ABS,       R.drawable.ic_muscle_abs),
        LibraryExerciseItem("Side Plank",               MuscleSection.ABS,       R.drawable.ic_muscle_abs),
        LibraryExerciseItem("Leg Raise",                MuscleSection.ABS,       R.drawable.ic_muscle_abs),
        LibraryExerciseItem("Hanging Knee Raise",       MuscleSection.ABS,       R.drawable.ic_muscle_abs),
        LibraryExerciseItem("Hanging Leg Raise",        MuscleSection.ABS,       R.drawable.ic_muscle_abs),
        LibraryExerciseItem("Russian Twist",            MuscleSection.ABS,       R.drawable.ic_muscle_abs),
        LibraryExerciseItem("Cable Crunch",             MuscleSection.ABS,       R.drawable.ic_muscle_abs),
        LibraryExerciseItem("Ab Wheel Rollout",         MuscleSection.ABS,       R.drawable.ic_muscle_abs),
        LibraryExerciseItem("Bicycle Crunch",           MuscleSection.ABS,       R.drawable.ic_muscle_abs),
        LibraryExerciseItem("Mountain Climber",         MuscleSection.ABS,       R.drawable.ic_muscle_abs),
        LibraryExerciseItem("Reverse Crunch",           MuscleSection.ABS,       R.drawable.ic_muscle_abs),
        LibraryExerciseItem("Dead Bug",                 MuscleSection.ABS,       R.drawable.ic_muscle_abs),
        LibraryExerciseItem("V-Up",                     MuscleSection.ABS,       R.drawable.ic_muscle_abs),

        // ── LEGS ───────────────────────────────────────────────
        LibraryExerciseItem("Squat",                    MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Front Squat",              MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Hack Squat",               MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Leg Press",                MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Romanian Deadlift",        MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Sumo Deadlift",            MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Leg Curl",                 MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Lying Leg Curl",           MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Leg Extension",            MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Bulgarian Split Squat",    MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Lunge",                    MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Walking Lunge",            MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Hip Thrust",               MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Glute Bridge",             MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Calf Raise",               MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Seated Calf Raise",        MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Step-Up",                  MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Goblet Squat",             MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
        LibraryExerciseItem("Nordic Curl",              MuscleSection.LEGS,      R.drawable.ic_muscle_legs),
    )

    fun filteredBy(section: MuscleSection?, query: String): List<LibraryExerciseItem> {
        return all.filter { item ->
            val matchSection = section == null || item.section == section
            val matchQuery = query.isBlank() || item.name.contains(query, ignoreCase = true)
            matchSection && matchQuery
        }
    }
}
