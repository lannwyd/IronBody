package com.ironlog.gym.domain.repository

import com.ironlog.gym.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getExercises(): Flow<List<Exercise>>
    fun getExercise(id: Long): Flow<Exercise?>
    suspend fun addExercise(exercise: Exercise): Long
    suspend fun updateExercise(exercise: Exercise)
    suspend fun updateWeight(id: Long, newWeight: Float)
    suspend fun updateSortOrders(orderedIds: List<Long>)
    suspend fun deleteExercise(exercise: Exercise, deleteImageFile: Boolean = true)
    suspend fun restoreExercise(exercise: Exercise): Long
}
