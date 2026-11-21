package com.ironlog.gym.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ironlog.gym.domain.model.SplitDay

@Entity(
    tableName = "split_days",
    foreignKeys = [
        ForeignKey(
            entity = GymSplitEntity::class,
            parentColumns = ["id"],
            childColumns = ["split_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["split_id"])
    ]
)
data class SplitDayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "split_id")
    val splitId: Long,
    val name: String,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0
) {
    fun toDomain(exerciseCount: Int = 0): SplitDay = SplitDay(
        id = id,
        splitId = splitId,
        name = name,
        sortOrder = sortOrder,
        exerciseCount = exerciseCount
    )

    companion object {
        fun fromDomain(day: SplitDay): SplitDayEntity = SplitDayEntity(
            id = day.id,
            splitId = day.splitId,
            name = day.name,
            sortOrder = day.sortOrder
        )
    }
}
