package com.ironlog.gym.di

import android.content.Context
import androidx.room.Room
import com.ironlog.gym.data.local.IronLogDatabase
import com.ironlog.gym.data.local.dao.BodyWeightDao
import com.ironlog.gym.data.local.dao.ExerciseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideIronLogDatabase(
        @ApplicationContext context: Context
    ): IronLogDatabase {
        return Room.databaseBuilder(
            context,
            IronLogDatabase::class.java,
            IronLogDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideExerciseDao(database: IronLogDatabase): ExerciseDao {
        return database.exerciseDao()
    }

    @Provides
    fun provideBodyWeightDao(database: IronLogDatabase): BodyWeightDao {
        return database.bodyWeightDao()
    }
}
