package com.ironlog.gym.domain.repository

import com.ironlog.gym.domain.model.BodyWeight
import kotlinx.coroutines.flow.Flow

interface BodyWeightRepository {
    fun getAllEntries(): Flow<List<BodyWeight>>
    fun getEntry(id: Long): Flow<BodyWeight?>
    suspend fun addEntry(entry: BodyWeight): Long
    suspend fun updateEntry(entry: BodyWeight)
    suspend fun deleteEntry(id: Long, receiptImagePath: String? = null)
}
