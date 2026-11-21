package com.ironlog.gym.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "split_day_exercises",
    foreignKeys = [
        ForeignKey(
            entity = SplitDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["split_day_id"],
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
        Index(value = ["split_day_id"]),
        Index(value = ["exercise_id"])
    ]
)
data class SplitDayExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "split_day_id")
    val splitDayId: Long,
    @ColumnInfo(name = "exercise_id")
    val exerciseId: Long,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0
)
