package com.ironlog.gym.data.repository

import com.ironlog.gym.data.local.dao.ExerciseDao
import com.ironlog.gym.data.local.dao.SplitDao
import com.ironlog.gym.data.local.entity.ExerciseEntity
import com.ironlog.gym.data.local.entity.GymSplitEntity
import com.ironlog.gym.data.local.entity.SplitDayEntity
import com.ironlog.gym.data.local.entity.SplitDayExerciseEntity
import com.ironlog.gym.domain.model.FamousSplitPreset
import com.ironlog.gym.domain.model.GymSplit
import com.ironlog.gym.domain.model.SharedExercise
import com.ironlog.gym.domain.model.SharedSplit
import com.ironlog.gym.domain.model.SharedSplitDay
import com.ironlog.gym.domain.model.SplitDay
import com.ironlog.gym.domain.model.SplitDayExercise
import com.ironlog.gym.domain.repository.SplitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SplitRepositoryImpl @Inject constructor(
    private val splitDao: SplitDao,
    private val exerciseDao: ExerciseDao
) : SplitRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
        encodeDefaults = true
    }

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

    override suspend fun exportSplit(splitId: Long): SharedSplit? {
        val splitEntity = splitDao.getSplitByIdSync(splitId) ?: return null
        val days = splitDao.getDaysForSplitSync(splitId)

        val sharedDays = days.map { dayEntity ->
            val exercisesWithDetails = splitDao.getExercisesForDaySync(dayEntity.id)
            val sharedExercises = exercisesWithDetails.map { item ->
                SharedExercise(
                    name = item.exercise.name,
                    weight = item.exercise.weight,
                    unit = item.exercise.unit,
                    section = item.exercise.section,
                    note = item.exercise.note,
                    sortOrder = item.daySortOrder
                )
            }
            SharedSplitDay(
                name = dayEntity.name,
                sortOrder = dayEntity.sortOrder,
                exercises = sharedExercises
            )
        }

        return SharedSplit(
            version = 1,
            app = "IronBody",
            name = splitEntity.name,
            description = splitEntity.description,
            days = sharedDays
        )
    }

    override suspend fun exportSplitToShareText(splitId: Long): String? {
        val sharedSplit = exportSplit(splitId) ?: return null
        val jsonString = json.encodeToString(sharedSplit)
        return buildString {
            appendLine("🏋️ IronBody Workout Split: ${sharedSplit.name}")
            appendLine("${sharedSplit.days.size} Days • ${sharedSplit.days.sumOf { it.exercises.size }} Exercises")
            appendLine("Copy this entire message and open IronBody -> Splits -> Import Split to load it!")
            appendLine()
            appendLine("--- BEGIN IRONBODY SPLIT ---")
            appendLine(jsonString)
            appendLine("--- END IRONBODY SPLIT ---")
        }
    }

    override fun parseSplitFromText(rawText: String): SharedSplit? {
        return try {
            val clean = rawText.trim()
            val beginMarker = "--- BEGIN IRONBODY SPLIT ---"
            val endMarker = "--- END IRONBODY SPLIT ---"

            val jsonContent = if (clean.contains(beginMarker) && clean.contains(endMarker)) {
                clean.substringAfter(beginMarker).substringBefore(endMarker).trim()
            } else {
                val firstBrace = clean.indexOf('{')
                val lastBrace = clean.lastIndexOf('}')
                if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
                    clean.substring(firstBrace, lastBrace + 1).trim()
                } else {
                    clean
                }
            }

            json.decodeFromString<SharedSplit>(jsonContent)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun importSplit(sharedSplit: SharedSplit): Long {
        splitDao.clearActiveSplits()
        val splitEntity = GymSplitEntity(
            name = sharedSplit.name.ifBlank { "Imported Split" },
            description = sharedSplit.description,
            isActive = true
        )
        val splitId = splitDao.insertSplit(splitEntity)

        for (day in sharedSplit.days) {
            val dayId = splitDao.insertDay(
                SplitDayEntity(
                    splitId = splitId,
                    name = day.name.ifBlank { "Workout Day" },
                    sortOrder = day.sortOrder
                )
            )

            for (sharedEx in day.exercises) {
                // Find or create global exercise
                val existing = exerciseDao.getExerciseByName(sharedEx.name.trim())
                val exerciseId = if (existing != null) {
                    existing.id
                } else {
                    val maxSortOrder = exerciseDao.getMaxSortOrder()
                    exerciseDao.insertExercise(
                        ExerciseEntity(
                            name = sharedEx.name.trim(),
                            weight = if (sharedEx.weight < 0f) 0f else sharedEx.weight,
                            unit = sharedEx.unit.ifBlank { "kg" },
                            imagePath = null,
                            sortOrder = maxSortOrder + 1,
                            createdAt = System.currentTimeMillis(),
                            note = sharedEx.note,
                            section = sharedEx.section.ifBlank { "chest" }
                        )
                    )
                }

                splitDao.insertSplitDayExercise(
                    SplitDayExerciseEntity(
                        splitDayId = dayId,
                        exerciseId = exerciseId,
                        sortOrder = sharedEx.sortOrder
                    )
                )
            }
        }

        return splitId
    }
}
