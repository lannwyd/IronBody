package com.ironlog.gym.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "session_exercise_logs",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["session_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["session_id"]),
        Index(value = ["exercise_id"])
    ]
)
data class SessionExerciseLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "session_id")
    val sessionId: Long,
    @ColumnInfo(name = "exercise_id")
    val exerciseId: Long,
    @ColumnInfo(name = "exercise_name")
    val exerciseName: String,
    @ColumnInfo(name = "exercise_weight")
    val exerciseWeight: Float,
    @ColumnInfo(name = "exercise_unit")
    val exerciseUnit: String,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int,
    @ColumnInfo(name = "chrono_duration_ms")
    val chronoDurationMs: Long = 0,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false
)
