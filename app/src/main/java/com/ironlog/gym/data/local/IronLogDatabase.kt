package com.ironlog.gym.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ironlog.gym.data.local.dao.ExerciseDao
import com.ironlog.gym.data.local.entity.ExerciseEntity

@Database(
    entities = [
        ExerciseEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class IronLogDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        const val DATABASE_NAME = "ironlog_database.db"
    }
}
