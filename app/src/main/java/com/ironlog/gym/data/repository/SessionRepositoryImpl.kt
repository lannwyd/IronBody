package com.ironlog.gym.data.repository

import com.ironlog.gym.data.local.dao.SessionDao
import com.ironlog.gym.data.local.entity.SessionExerciseLogEntity
import com.ironlog.gym.data.local.entity.WorkoutSessionEntity
import com.ironlog.gym.domain.model.SessionExerciseLog
import com.ironlog.gym.domain.model.WorkoutSession
import com.ironlog.gym.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : SessionRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override suspend fun startSession(splitId: Long, splitDayId: Long, splitDayName: String): Long {
        val now = System.currentTimeMillis()
        val entity = WorkoutSessionEntity(
            splitId = splitId,
            splitDayId = splitDayId,
            splitDayName = splitDayName,
            startTime = now,
            date = dateFormat.format(Date(now))
        )
        return sessionDao.insertSession(entity)
    }

    override suspend fun finishSession(sessionId: Long, totalDurationMs: Long) {
        val session = sessionDao.getSessionById(sessionId) ?: return
        sessionDao.updateSession(
            session.copy(
                endTime = System.currentTimeMillis(),
                totalDurationMs = totalDurationMs
            )
        )
    }

    override suspend fun getSessionById(sessionId: Long): WorkoutSession? {
        return sessionDao.getSessionById(sessionId)?.toDomain()
    }

    override suspend fun createExerciseLogs(sessionId: Long, exercises: List<SessionExerciseLog>) {
        val entities = exercises.map {
            SessionExerciseLogEntity(
                sessionId = sessionId,
                exerciseId = it.exerciseId,
                exerciseName = it.exerciseName,
                exerciseWeight = it.exerciseWeight,
                exerciseUnit = it.exerciseUnit,
                sortOrder = it.sortOrder,
                chronoDurationMs = 0,
                isCompleted = false
            )
        }
        sessionDao.insertExerciseLogs(entities)
    }

    override suspend fun updateExerciseLogDuration(logId: Long, durationMs: Long) {
        sessionDao.updateLogDuration(logId, durationMs)
    }

    override suspend fun markExerciseCompleted(logId: Long, completed: Boolean) {
        sessionDao.updateLogCompleted(logId, completed)
    }

    override fun getLogsForSession(sessionId: Long): Flow<List<SessionExerciseLog>> {
        return sessionDao.getLogsForSessionFlow(sessionId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getLogsForSessionSync(sessionId: Long): List<SessionExerciseLog> {
        return sessionDao.getLogsForSession(sessionId).map { it.toDomain() }
    }

    override fun getAllSessions(): Flow<List<WorkoutSession>> {
        return sessionDao.getAllSessions().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun deleteSession(sessionId: Long) {
        sessionDao.deleteLogsForSession(sessionId)
        sessionDao.deleteSession(sessionId)
    }

    private fun WorkoutSessionEntity.toDomain() = WorkoutSession(
        id = id,
        splitId = splitId,
        splitDayId = splitDayId,
        splitDayName = splitDayName,
        startTime = startTime,
        endTime = endTime,
        totalDurationMs = totalDurationMs,
        date = date
    )

    private fun SessionExerciseLogEntity.toDomain() = SessionExerciseLog(
        id = id,
        sessionId = sessionId,
        exerciseId = exerciseId,
        exerciseName = exerciseName,
        exerciseWeight = exerciseWeight,
        exerciseUnit = exerciseUnit,
        sortOrder = sortOrder,
        chronoDurationMs = chronoDurationMs,
        isCompleted = isCompleted
    )
}
