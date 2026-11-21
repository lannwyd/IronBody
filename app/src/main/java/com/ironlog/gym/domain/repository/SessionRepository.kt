package com.ironlog.gym.domain.repository

import com.ironlog.gym.domain.model.SessionExerciseLog
import com.ironlog.gym.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun startSession(splitId: Long, splitDayId: Long, splitDayName: String): Long
    suspend fun finishSession(sessionId: Long, totalDurationMs: Long)
    suspend fun getSessionById(sessionId: Long): WorkoutSession?

    suspend fun createExerciseLogs(sessionId: Long, exercises: List<SessionExerciseLog>)
    suspend fun updateExerciseLogDuration(logId: Long, durationMs: Long)
    suspend fun markExerciseCompleted(logId: Long, completed: Boolean)
    fun getLogsForSession(sessionId: Long): Flow<List<SessionExerciseLog>>
    suspend fun getLogsForSessionSync(sessionId: Long): List<SessionExerciseLog>

    fun getAllSessions(): Flow<List<WorkoutSession>>
}
