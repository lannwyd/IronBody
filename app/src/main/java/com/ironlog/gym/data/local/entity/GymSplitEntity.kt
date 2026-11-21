package com.ironlog.gym.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ironlog.gym.domain.model.GymSplit

@Entity(tableName = "gym_splits")
data class GymSplitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): GymSplit = GymSplit(
        id = id,
        name = name,
        description = description,
        isActive = isActive,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(split: GymSplit): GymSplitEntity = GymSplitEntity(
            id = split.id,
            name = split.name,
            description = split.description,
            isActive = split.isActive,
            createdAt = split.createdAt
        )
    }
}
