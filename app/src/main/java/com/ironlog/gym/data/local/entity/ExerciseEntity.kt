package com.ironlog.gym.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ironlog.gym.domain.model.Exercise

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val weight: Float,
    val unit: String,
    @ColumnInfo(name = "image_path")
    val imagePath: String?,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    val note: String,
    @ColumnInfo(name = "section", defaultValue = "chest")
    val section: String = "chest"
) {
    fun toDomain(): Exercise = Exercise(
        id = id,
        name = name,
        weight = weight,
        unit = unit,
        imagePath = imagePath,
        sortOrder = sortOrder,
        createdAt = createdAt,
        note = note,
        section = section
    )

    companion object {
        fun fromDomain(exercise: Exercise): ExerciseEntity = ExerciseEntity(
            id = exercise.id,
            name = exercise.name,
            weight = exercise.weight,
            unit = exercise.unit,
            imagePath = exercise.imagePath,
            sortOrder = exercise.sortOrder,
            createdAt = exercise.createdAt,
            note = exercise.note,
            section = exercise.section
        )
    }
}
