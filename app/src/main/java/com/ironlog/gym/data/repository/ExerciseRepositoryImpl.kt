package com.ironlog.gym.data.repository

import com.ironlog.gym.data.local.dao.ExerciseDao
import com.ironlog.gym.data.local.entity.ExerciseEntity
import com.ironlog.gym.domain.model.Exercise
import com.ironlog.gym.domain.repository.ExerciseRepository
import com.ironlog.gym.util.ImageStorageManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val imageStorageManager: ImageStorageManager
) : ExerciseRepository {

    override fun getExercises(): Flow<List<Exercise>> {
        return exerciseDao.getAllExercises().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getExercise(id: Long): Flow<Exercise?> {
        return exerciseDao.getExerciseById(id).map { it?.toDomain() }
    }

    override suspend fun addExercise(exercise: Exercise): Long {
        val maxOrder = exerciseDao.getMaxSortOrder()
        val order = if (exercise.sortOrder <= 0) maxOrder + 1 else exercise.sortOrder
        val entity = ExerciseEntity.fromDomain(exercise.copy(sortOrder = order))
        return exerciseDao.insertExercise(entity)
    }

    override suspend fun updateExercise(exercise: Exercise) {
        exerciseDao.updateExercise(ExerciseEntity.fromDomain(exercise))
    }

    override suspend fun updateWeight(id: Long, newWeight: Float) {
        val safeWeight = if (newWeight < 0f) 0f else newWeight
        exerciseDao.updateWeight(id, safeWeight)
    }

    override suspend fun updateSortOrders(orderedIds: List<Long>) {
        exerciseDao.updateSortOrders(orderedIds)
    }

    override suspend fun deleteExercise(exercise: Exercise, deleteImageFile: Boolean) {
        exerciseDao.deleteExerciseById(exercise.id)
        if (deleteImageFile && !exercise.imagePath.isNullOrBlank()) {
            imageStorageManager.deleteImageFile(exercise.imagePath)
        }
    }

    override suspend fun restoreExercise(exercise: Exercise): Long {
        return exerciseDao.insertExercise(ExerciseEntity.fromDomain(exercise))
    }
}
