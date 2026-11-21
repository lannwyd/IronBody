package com.ironlog.gym.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ironlog.gym.data.local.entity.SessionExerciseLogEntity
import com.ironlog.gym.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity): Long

    @Update
    suspend fun updateSession(session: WorkoutSessionEntity)

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: Long): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_sessions WHERE end_time IS NULL LIMIT 1")
    suspend fun getActiveSession(): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_sessions ORDER BY start_time DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE date = :date ORDER BY start_time DESC")
    fun getSessionsForDate(date: String): Flow<List<WorkoutSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLog(log: SessionExerciseLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLogs(logs: List<SessionExerciseLogEntity>)

    @Update
    suspend fun updateExerciseLog(log: SessionExerciseLogEntity)

    @Query("SELECT * FROM session_exercise_logs WHERE session_id = :sessionId ORDER BY sort_order ASC")
    suspend fun getLogsForSession(sessionId: Long): List<SessionExerciseLogEntity>

    @Query("SELECT * FROM session_exercise_logs WHERE session_id = :sessionId ORDER BY sort_order ASC")
    fun getLogsForSessionFlow(sessionId: Long): Flow<List<SessionExerciseLogEntity>>

    @Query("UPDATE session_exercise_logs SET chrono_duration_ms = :durationMs WHERE id = :logId")
    suspend fun updateLogDuration(logId: Long, durationMs: Long)

    @Query("UPDATE session_exercise_logs SET is_completed = :completed WHERE id = :logId")
    suspend fun updateLogCompleted(logId: Long, completed: Boolean)
}
