package com.ironlog.gym.domain.repository

import com.ironlog.gym.domain.model.FamousSplitPreset
import com.ironlog.gym.domain.model.GymSplit
import com.ironlog.gym.domain.model.SharedSplit
import com.ironlog.gym.domain.model.SplitDay
import com.ironlog.gym.domain.model.SplitDayExercise
import kotlinx.coroutines.flow.Flow

interface SplitRepository {
    fun getAllSplits(): Flow<List<GymSplit>>
    fun getActiveSplit(): Flow<GymSplit?>
    fun getSplitById(splitId: Long): Flow<GymSplit?>
    suspend fun createSplit(name: String, description: String = "", days: List<String> = emptyList()): Long
    suspend fun createSplitFromPreset(preset: FamousSplitPreset): Long
    suspend fun updateSplitName(splitId: Long, newName: String, description: String = "")
    suspend fun setActiveSplit(splitId: Long)
    suspend fun deleteSplit(splitId: Long)

    fun getDaysForSplit(splitId: Long): Flow<List<SplitDay>>
    fun getDayById(dayId: Long): Flow<SplitDay?>
    suspend fun addDay(splitId: Long, name: String): Long
    suspend fun updateDayName(dayId: Long, newName: String)
    suspend fun updateDaySortOrders(orderedDayIds: List<Long>)
    suspend fun deleteDay(dayId: Long)

    fun getExercisesForDay(splitDayId: Long): Flow<List<SplitDayExercise>>
    fun getExerciseIdsForDay(splitDayId: Long): Flow<List<Long>>
    suspend fun addExercisesToDay(splitDayId: Long, exerciseIds: List<Long>)
    suspend fun removeExerciseFromDay(splitDayExerciseId: Long)
    suspend fun removeExerciseFromDayByExerciseId(splitDayId: Long, exerciseId: Long)
    suspend fun updateDayExerciseSortOrders(orderedSplitDayExerciseIds: List<Long>)
    suspend fun ensureDefaultSplitExists(): Long

    // Sharing & Importing
    suspend fun exportSplit(splitId: Long): SharedSplit?
    suspend fun exportSplitToShareText(splitId: Long): String?
    fun parseSplitFromText(rawText: String): SharedSplit?
    suspend fun importSplit(sharedSplit: SharedSplit): Long
}
