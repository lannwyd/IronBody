package com.ironlog.gym.data.local.entity

import androidx.room.Embedded
import com.ironlog.gym.domain.model.SplitDay

data class SplitDayWithCount(
    @Embedded val day: SplitDayEntity,
    val exerciseCount: Int
) {
    fun toDomain(): SplitDay = day.toDomain(exerciseCount = exerciseCount)
}
