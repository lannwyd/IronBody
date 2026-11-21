package com.ironlog.gym.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_sessions",
    foreignKeys = [
        ForeignKey(
            entity = GymSplitEntity::class,
            parentColumns = ["id"],
            childColumns = ["split_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SplitDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["split_day_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["split_id"]),
        Index(value = ["split_day_id"]),
        Index(value = ["date"])
    ]
)
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "split_id")
    val splitId: Long,
    @ColumnInfo(name = "split_day_id")
    val splitDayId: Long,
    @ColumnInfo(name = "split_day_name")
    val splitDayName: String,
    @ColumnInfo(name = "start_time")
    val startTime: Long,
    @ColumnInfo(name = "end_time")
    val endTime: Long? = null,
    @ColumnInfo(name = "total_duration_ms")
    val totalDurationMs: Long = 0,
    @ColumnInfo(name = "date")
    val date: String
)
