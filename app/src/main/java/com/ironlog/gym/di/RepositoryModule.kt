package com.ironlog.gym.di

import com.ironlog.gym.data.repository.BodyWeightRepositoryImpl
import com.ironlog.gym.data.repository.ExerciseRepositoryImpl
import com.ironlog.gym.data.repository.SplitRepositoryImpl
import com.ironlog.gym.domain.repository.BodyWeightRepository
import com.ironlog.gym.domain.repository.ExerciseRepository
import com.ironlog.gym.domain.repository.SplitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(
        exerciseRepositoryImpl: ExerciseRepositoryImpl
    ): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindBodyWeightRepository(
        bodyWeightRepositoryImpl: BodyWeightRepositoryImpl
    ): BodyWeightRepository

    @Binds
    @Singleton
    abstract fun bindSplitRepository(
        splitRepositoryImpl: SplitRepositoryImpl
    ): SplitRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        sessionRepositoryImpl: com.ironlog.gym.data.repository.SessionRepositoryImpl
    ): com.ironlog.gym.domain.repository.SessionRepository
}
