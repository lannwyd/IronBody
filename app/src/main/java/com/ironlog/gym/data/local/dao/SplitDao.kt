package com.ironlog.gym.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ironlog.gym.data.local.entity.GymSplitEntity
import com.ironlog.gym.data.local.entity.SplitDayEntity
import com.ironlog.gym.data.local.entity.SplitDayExerciseEntity
import com.ironlog.gym.data.local.entity.SplitDayExerciseWithDetails
import com.ironlog.gym.data.local.entity.SplitDayWithCount
import kotlinx.coroutines.flow.Flow

@Dao
interface SplitDao {

    // === Gym Splits ===
    @Query("SELECT * FROM gym_splits ORDER BY created_at DESC")
    fun getAllSplits(): Flow<List<GymSplitEntity>>

    @Query("SELECT * FROM gym_splits WHERE is_active = 1 LIMIT 1")
    fun getActiveSplit(): Flow<GymSplitEntity?>

    @Query("SELECT * FROM gym_splits WHERE is_active = 1 LIMIT 1")
    suspend fun getActiveSplitSync(): GymSplitEntity?

    @Query("SELECT * FROM gym_splits WHERE id = :id LIMIT 1")
    fun getSplitById(id: Long): Flow<GymSplitEntity?>

    @Query("SELECT * FROM gym_splits WHERE id = :id LIMIT 1")
    suspend fun getSplitByIdSync(id: Long): GymSplitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSplit(split: GymSplitEntity): Long

    @Update
    suspend fun updateSplit(split: GymSplitEntity)

    @Query("UPDATE gym_splits SET name = :newName, description = :description WHERE id = :splitId")
    suspend fun updateSplitDetails(splitId: Long, newName: String, description: String)

    @Query("DELETE FROM gym_splits WHERE id = :id")
    suspend fun deleteSplitById(id: Long)

    @Query("UPDATE gym_splits SET is_active = 0")
    suspend fun clearActiveSplits()

    @Query("UPDATE gym_splits SET is_active = 1 WHERE id = :splitId")
    suspend fun markSplitActive(splitId: Long)

    @Transaction
    suspend fun setActiveSplit(splitId: Long) {
        clearActiveSplits()
        markSplitActive(splitId)
    }

    // === Split Days ===
    @Query("""
        SELECT sd.id, sd.split_id, sd.name, sd.sort_order, COUNT(sde.id) AS exerciseCount
        FROM split_days sd
        LEFT JOIN split_day_exercises sde ON sd.id = sde.split_day_id
        WHERE sd.split_id = :splitId
        GROUP BY sd.id
        ORDER BY sd.sort_order ASC, sd.id ASC
    """)
    fun getDaysWithCountForSplit(splitId: Long): Flow<List<SplitDayWithCount>>

    @Query("SELECT * FROM split_days WHERE split_id = :splitId ORDER BY sort_order ASC, id ASC")
    suspend fun getDaysForSplitSync(splitId: Long): List<SplitDayEntity>

    @Query("SELECT * FROM split_days WHERE id = :dayId LIMIT 1")
    fun getDayById(dayId: Long): Flow<SplitDayEntity?>

    @Query("SELECT COALESCE(MAX(sort_order), -1) FROM split_days WHERE split_id = :splitId")
    suspend fun getMaxDaySortOrder(splitId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(day: SplitDayEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDays(days: List<SplitDayEntity>): List<Long>

    @Update
    suspend fun updateDay(day: SplitDayEntity)

    @Query("UPDATE split_days SET name = :newName WHERE id = :dayId")
    suspend fun updateDayName(dayId: Long, newName: String)

    @Query("UPDATE split_days SET sort_order = :sortOrder WHERE id = :dayId")
    suspend fun updateDaySortOrder(dayId: Long, sortOrder: Int)

    @Transaction
    suspend fun updateDaySortOrders(orderedDayIds: List<Long>) {
        orderedDayIds.forEachIndexed { index, id ->
            updateDaySortOrder(id, index)
        }
    }

    @Query("DELETE FROM split_days WHERE id = :dayId")
    suspend fun deleteDayById(dayId: Long)

    // === Split Day Exercises ===
    @Query("""
        SELECT 
            sde.id AS split_day_exercise_id,
            sde.split_day_id AS split_day_id,
            sde.sort_order AS day_sort_order,
            e.id AS id,
            e.name AS name,
            e.weight AS weight,
            e.unit AS unit,
            e.image_path AS image_path,
            e.sort_order AS sort_order,
            e.created_at AS created_at,
            e.note AS note,
            e.section AS section
        FROM split_day_exercises sde
        INNER JOIN exercises e ON sde.exercise_id = e.id
        WHERE sde.split_day_id = :splitDayId
        ORDER BY sde.sort_order ASC, sde.id ASC
    """)
    fun getExercisesForDay(splitDayId: Long): Flow<List<SplitDayExerciseWithDetails>>

    @Query("""
        SELECT 
            sde.id AS split_day_exercise_id,
            sde.split_day_id AS split_day_id,
            sde.sort_order AS day_sort_order,
            e.id AS id,
            e.name AS name,
            e.weight AS weight,
            e.unit AS unit,
            e.image_path AS image_path,
            e.sort_order AS sort_order,
            e.created_at AS created_at,
            e.note AS note,
            e.section AS section
        FROM split_day_exercises sde
        INNER JOIN exercises e ON sde.exercise_id = e.id
        WHERE sde.split_day_id = :splitDayId
        ORDER BY sde.sort_order ASC, sde.id ASC
    """)
    suspend fun getExercisesForDaySync(splitDayId: Long): List<SplitDayExerciseWithDetails>

    @Query("SELECT exercise_id FROM split_day_exercises WHERE split_day_id = :splitDayId")
    fun getExerciseIdsForDay(splitDayId: Long): Flow<List<Long>>

    @Query("SELECT COALESCE(MAX(sort_order), -1) FROM split_day_exercises WHERE split_day_id = :splitDayId")
    suspend fun getMaxExerciseSortOrder(splitDayId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSplitDayExercise(entry: SplitDayExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSplitDayExercises(entries: List<SplitDayExerciseEntity>)

    @Query("DELETE FROM split_day_exercises WHERE id = :splitDayExerciseId")
    suspend fun deleteSplitDayExerciseById(splitDayExerciseId: Long)

    @Query("DELETE FROM split_day_exercises WHERE split_day_id = :splitDayId AND exercise_id = :exerciseId")
    suspend fun removeExerciseFromDay(splitDayId: Long, exerciseId: Long)

    @Query("UPDATE split_day_exercises SET sort_order = :sortOrder WHERE id = :splitDayExerciseId")
    suspend fun updateSplitDayExerciseSortOrder(splitDayExerciseId: Long, sortOrder: Int)

    @Transaction
    suspend fun updateSplitDayExerciseSortOrders(orderedSplitDayExerciseIds: List<Long>) {
        orderedSplitDayExerciseIds.forEachIndexed { index, id ->
            updateSplitDayExerciseSortOrder(id, index)
        }
    }
}
