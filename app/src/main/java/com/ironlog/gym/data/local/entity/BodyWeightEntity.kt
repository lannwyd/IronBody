package com.ironlog.gym.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ironlog.gym.domain.model.BodyWeight

@Entity(tableName = "body_weight")
data class BodyWeightEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weight: Float,
    val unit: String,
    val date: Long,
    @ColumnInfo(name = "receipt_image_path")
    val receiptImagePath: String?,
    val note: String
) {
    fun toDomain(): BodyWeight = BodyWeight(
        id = id,
        weight = weight,
        unit = unit,
        date = date,
        receiptImagePath = receiptImagePath,
        note = note
    )

    companion object {
        fun fromDomain(bodyWeight: BodyWeight): BodyWeightEntity = BodyWeightEntity(
            id = bodyWeight.id,
            weight = bodyWeight.weight,
            unit = bodyWeight.unit,
            date = bodyWeight.date,
            receiptImagePath = bodyWeight.receiptImagePath,
            note = bodyWeight.note
        )
    }
}
