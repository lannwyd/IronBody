package com.ironlog.gym.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ironlog.gym.data.local.entity.BodyWeightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyWeightDao {

    @Query("SELECT * FROM body_weight ORDER BY date DESC")
    fun getAllEntries(): Flow<List<BodyWeightEntity>>

    @Query("SELECT * FROM body_weight WHERE id = :id LIMIT 1")
    fun getEntryById(id: Long): Flow<BodyWeightEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: BodyWeightEntity): Long

    @Update
    suspend fun updateEntry(entry: BodyWeightEntity)

    @Query("DELETE FROM body_weight WHERE id = :id")
    suspend fun deleteEntryById(id: Long)
}
