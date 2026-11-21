package com.ironlog.gym.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ironlog.gym.data.local.dao.BodyWeightDao
import com.ironlog.gym.data.local.dao.ExerciseDao
import com.ironlog.gym.data.local.dao.SplitDao
import com.ironlog.gym.data.local.entity.BodyWeightEntity
import com.ironlog.gym.data.local.entity.ExerciseEntity
import com.ironlog.gym.data.local.entity.GymSplitEntity
import com.ironlog.gym.data.local.entity.SplitDayEntity
import com.ironlog.gym.data.local.entity.SplitDayExerciseEntity

@Database(
    entities = [
        ExerciseEntity::class,
        BodyWeightEntity::class,
        GymSplitEntity::class,
        SplitDayEntity::class,
        SplitDayExerciseEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class IronLogDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun bodyWeightDao(): BodyWeightDao
    abstract fun splitDao(): SplitDao

    companion object {
        const val DATABASE_NAME = "ironlog_database.db"
    }
}
