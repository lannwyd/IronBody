package com.ironlog.gym.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ironlog.gym.data.local.dao.BodyWeightDao
import com.ironlog.gym.data.local.dao.ExerciseDao
import com.ironlog.gym.data.local.entity.BodyWeightEntity
import com.ironlog.gym.data.local.entity.ExerciseEntity

@Database(
    entities = [
        ExerciseEntity::class,
        BodyWeightEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class IronLogDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun bodyWeightDao(): BodyWeightDao

    companion object {
        const val DATABASE_NAME = "ironlog_database.db"
    }
}
