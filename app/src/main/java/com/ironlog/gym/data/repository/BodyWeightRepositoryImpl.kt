package com.ironlog.gym.data.repository

import com.ironlog.gym.data.local.dao.BodyWeightDao
import com.ironlog.gym.data.local.entity.BodyWeightEntity
import com.ironlog.gym.domain.model.BodyWeight
import com.ironlog.gym.domain.repository.BodyWeightRepository
import com.ironlog.gym.util.ImageStorageManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BodyWeightRepositoryImpl @Inject constructor(
    private val bodyWeightDao: BodyWeightDao,
    private val imageStorageManager: ImageStorageManager
) : BodyWeightRepository {

    override fun getAllEntries(): Flow<List<BodyWeight>> {
        return bodyWeightDao.getAllEntries().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getEntry(id: Long): Flow<BodyWeight?> {
        return bodyWeightDao.getEntryById(id).map { it?.toDomain() }
    }

    override suspend fun addEntry(entry: BodyWeight): Long {
        return bodyWeightDao.insertEntry(BodyWeightEntity.fromDomain(entry))
    }

    override suspend fun updateEntry(entry: BodyWeight) {
        bodyWeightDao.updateEntry(BodyWeightEntity.fromDomain(entry))
    }

    override suspend fun deleteEntry(id: Long, receiptImagePath: String?) {
        bodyWeightDao.deleteEntryById(id)
        if (!receiptImagePath.isNullOrBlank()) {
            imageStorageManager.deleteImageFile(receiptImagePath)
        }
    }
}
