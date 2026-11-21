package com.ironlog.gym.data.repository

import com.ironlog.gym.data.local.dao.SplitDao
import com.ironlog.gym.data.local.entity.GymSplitEntity
import com.ironlog.gym.data.local.entity.SplitDayEntity
import com.ironlog.gym.data.local.entity.SplitDayExerciseEntity
import com.ironlog.gym.domain.model.FamousSplitPreset
import com.ironlog.gym.domain.model.GymSplit
import com.ironlog.gym.domain.model.SplitDay
import com.ironlog.gym.domain.model.SplitDayExercise
import com.ironlog.gym.domain.repository.SplitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SplitRepositoryImpl @Inject constructor(
    private val splitDao: SplitDao
) : SplitRepository {

    override fun getAllSplits(): Flow<List<GymSplit>> {
        return splitDao.getAllSplits().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActiveSplit(): Flow<GymSplit?> {
        return splitDao.getActiveSplit().map { it?.toDomain() }
    }

    override fun getSplitById(splitId: Long): Flow<GymSplit?> {
        return splitDao.getSplitById(splitId).map { it?.toDomain() }
    }

    override suspend fun createSplit(name: String, description: String, days: List<String>): Long {
        splitDao.clearActiveSplits()
        val splitEntity = GymSplitEntity(
            name = name.trim(),
            description = description.trim(),
            isActive = true
        )
        val splitId = splitDao.insertSplit(splitEntity)

        val dayEntities = days.mapIndexed { index, dayName ->
            SplitDayEntity(
                splitId = splitId,
                name = dayName.trim(),
                sortOrder = index
            )
        }
        if (dayEntities.isNotEmpty()) {
            splitDao.insertDays(dayEntities)
        }
        return splitId
    }

    override suspend fun createSplitFromPreset(preset: FamousSplitPreset): Long {
        return createSplit(
            name = preset.name,
            description = preset.description,
            days = preset.defaultDays
        )
    }

    override suspend fun updateSplitName(splitId: Long, newName: String, description: String) {
        splitDao.updateSplitDetails(splitId, newName.trim(), description.trim())
    }

    override suspend fun setActiveSplit(splitId: Long) {
        splitDao.setActiveSplit(splitId)
    }

    override suspend fun deleteSplit(splitId: Long) {
        splitDao.deleteSplitById(splitId)
    }

    override fun getDaysForSplit(splitId: Long): Flow<List<SplitDay>> {
        return splitDao.getDaysWithCountForSplit(splitId).map { items ->
            items.map { it.toDomain() }
        }
    }

    override fun getDayById(dayId: Long): Flow<SplitDay?> {
        return splitDao.getDayById(dayId).map { it?.toDomain() }
    }

    override suspend fun addDay(splitId: Long, name: String): Long {
        val maxOrder = splitDao.getMaxDaySortOrder(splitId)
        val dayEntity = SplitDayEntity(
            splitId = splitId,
            name = name.trim(),
            sortOrder = maxOrder + 1
        )
        return splitDao.insertDay(dayEntity)
    }

    override suspend fun updateDayName(dayId: Long, newName: String) {
        splitDao.updateDayName(dayId, newName.trim())
    }

    override suspend fun updateDaySortOrders(orderedDayIds: List<Long>) {
        splitDao.updateDaySortOrders(orderedDayIds)
    }

    override suspend fun deleteDay(dayId: Long) {
        splitDao.deleteDayById(dayId)
    }

    override fun getExercisesForDay(splitDayId: Long): Flow<List<SplitDayExercise>> {
        return splitDao.getExercisesForDay(splitDayId).map { items ->
            items.map { it.toDomain() }
        }
    }

    override fun getExerciseIdsForDay(splitDayId: Long): Flow<List<Long>> {
        return splitDao.getExerciseIdsForDay(splitDayId)
    }

    override suspend fun addExercisesToDay(splitDayId: Long, exerciseIds: List<Long>) {
        val currentMaxOrder = splitDao.getMaxExerciseSortOrder(splitDayId)
        val entities = exerciseIds.mapIndexed { index, exerciseId ->
            SplitDayExerciseEntity(
                splitDayId = splitDayId,
                exerciseId = exerciseId,
                sortOrder = currentMaxOrder + 1 + index
            )
        }
        if (entities.isNotEmpty()) {
            splitDao.insertSplitDayExercises(entities)
        }
    }

    override suspend fun removeExerciseFromDay(splitDayExerciseId: Long) {
        splitDao.deleteSplitDayExerciseById(splitDayExerciseId)
    }

    override suspend fun removeExerciseFromDayByExerciseId(splitDayId: Long, exerciseId: Long) {
        splitDao.removeExerciseFromDay(splitDayId, exerciseId)
    }

    override suspend fun updateDayExerciseSortOrders(orderedSplitDayExerciseIds: List<Long>) {
        splitDao.updateSplitDayExerciseSortOrders(orderedSplitDayExerciseIds)
    }

    override suspend fun ensureDefaultSplitExists(): Long {
        val existingActive = splitDao.getActiveSplitSync()
        if (existingActive != null) {
            return existingActive.id
        }
        return createSplitFromPreset(FamousSplitPreset.ALL.first())
    }
}
