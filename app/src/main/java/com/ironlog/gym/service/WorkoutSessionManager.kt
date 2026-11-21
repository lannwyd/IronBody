package com.ironlog.gym.service

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.ContextCompat
import com.ironlog.gym.domain.model.SessionExerciseLog
import com.ironlog.gym.domain.repository.SessionRepository
import com.ironlog.gym.domain.repository.SplitRepository
import com.ironlog.gym.ui.screens.session.ExerciseTimerState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutSessionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionRepository: SessionRepository,
    private val splitRepository: SplitRepository
) {
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _sessionState = MutableStateFlow<WorkoutSessionState?>(null)
    val sessionState: StateFlow<WorkoutSessionState?> = _sessionState.asStateFlow()

    // Timing Jobs & Realtime anchors
    private var sessionChronoJob: Job? = null
    private var sessionChronoBaseTime: Long = 0L

    private var exerciseChronoJob: Job? = null
    private var exerciseChronoBaseTime: Long = 0L
    private var activeExerciseAccumulatedMs: Long = 0L

    private var restTimerJob: Job? = null
    private var restTimerTargetEndTime: Long = 0L
    private var activeRestExerciseIndex: Int = -1

    private var sessionTimerJob: Job? = null
    private var sessionTimerTargetEndTime: Long = 0L

    fun isWorkoutActive(): Boolean = _sessionState.value != null

    fun startWorkout(splitDayId: Long, splitId: Long, dayName: String) {
        val current = _sessionState.value
        if (current != null && current.splitDayId == splitDayId) {
            // Already running this workout
            return
        }

        managerScope.launch {
            val exercises = splitRepository.getExercisesForDay(splitDayId).first()
            val createdSessionId = sessionRepository.startSession(splitId, splitDayId, dayName)

            val initialLogs = exercises.mapIndexed { index, item ->
                SessionExerciseLog(
                    sessionId = createdSessionId,
                    exerciseId = item.exercise.id,
                    exerciseName = item.exercise.name,
                    exerciseWeight = item.exercise.weight,
                    exerciseUnit = item.exercise.unit,
                    sortOrder = index
                )
            }
            sessionRepository.createExerciseLogs(createdSessionId, initialLogs)

            val savedLogs = sessionRepository.getLogsForSessionSync(createdSessionId)

            val states = exercises.mapIndexed { index, item ->
                val log = savedLogs.getOrNull(index)
                ExerciseTimerState(
                    logId = log?.id ?: 0L,
                    exerciseId = item.exercise.id,
                    exerciseName = item.exercise.name,
                    exerciseWeight = item.exercise.weight,
                    exerciseUnit = item.exercise.unit,
                    sortOrder = index,
                    chronoElapsedMs = 0L,
                    isChronoRunning = false,
                    isCompleted = false
                )
            }

            _sessionState.value = WorkoutSessionState(
                sessionId = createdSessionId,
                splitId = splitId,
                splitDayId = splitDayId,
                dayName = dayName,
                sessionElapsedMs = 0L,
                isSessionRunning = true,
                exercises = states,
                activeExerciseIndex = -1
            )

            // Start Foreground Service
            startForegroundService()

            // Start Session Chrono
            startSessionChrono()
        }
    }

    private fun startForegroundService() {
        val intent = Intent(context, WorkoutSessionService::class.java).apply {
            action = WorkoutSessionService.ACTION_START_OR_UPDATE
        }
        try {
            ContextCompat.startForegroundService(context, intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopForegroundService() {
        val intent = Intent(context, WorkoutSessionService::class.java).apply {
            action = WorkoutSessionService.ACTION_STOP
        }
        context.startService(intent)
    }

    // ──────────────────────────────────────────────
    // SESSION CHRONOMETER
    // ──────────────────────────────────────────────

    private fun startSessionChrono() {
        val currentState = _sessionState.value ?: return
        sessionChronoBaseTime = SystemClock.elapsedRealtime() - currentState.sessionElapsedMs

        sessionChronoJob?.cancel()
        sessionChronoJob = managerScope.launch {
            while (isActive) {
                val elapsed = SystemClock.elapsedRealtime() - sessionChronoBaseTime
                _sessionState.update { it?.copy(sessionElapsedMs = elapsed, isSessionRunning = true) }
                delay(100)
            }
        }
    }

    // ──────────────────────────────────────────────
    // EXERCISE CHRONOMETER
    // ──────────────────────────────────────────────

    fun toggleExerciseChrono(exerciseIndex: Int? = null) {
        val state = _sessionState.value ?: return
        val currentActive = state.activeExerciseIndex

        val target = exerciseIndex ?: if (currentActive >= 0) currentActive else {
            val uncompleted = state.exercises.indexOfFirst { !it.isCompleted }
            if (uncompleted >= 0) uncompleted else 0
        }

        if (currentActive == target) {
            pauseExerciseChrono()
        } else {
            if (currentActive >= 0) {
                pauseExerciseChrono()
            }
            startExerciseChrono(target)
        }
    }

    private fun startExerciseChrono(index: Int) {
        val state = _sessionState.value ?: return
        if (index !in state.exercises.indices) return

        val exercise = state.exercises[index]
        activeExerciseAccumulatedMs = exercise.chronoElapsedMs
        exerciseChronoBaseTime = SystemClock.elapsedRealtime() - activeExerciseAccumulatedMs

        _sessionState.update { s ->
            s?.copy(
                activeExerciseIndex = index,
                exercises = s.exercises.mapIndexed { i, ex ->
                    if (i == index) ex.copy(isChronoRunning = true)
                    else ex.copy(isChronoRunning = false)
                }
            )
        }

        exerciseChronoJob?.cancel()
        exerciseChronoJob = managerScope.launch {
            while (isActive) {
                val elapsed = SystemClock.elapsedRealtime() - exerciseChronoBaseTime
                _sessionState.update { s ->
                    s?.copy(
                        exercises = s.exercises.mapIndexed { i, ex ->
                            if (i == index) ex.copy(chronoElapsedMs = elapsed) else ex
                        }
                    )
                }
                delay(100)
            }
        }
    }

    private fun pauseExerciseChrono() {
        exerciseChronoJob?.cancel()
        exerciseChronoJob = null

        val state = _sessionState.value ?: return
        val currentIdx = state.activeExerciseIndex
        if (currentIdx >= 0 && currentIdx in state.exercises.indices) {
            val ex = state.exercises[currentIdx]
            val duration = ex.chronoElapsedMs

            managerScope.launch {
                sessionRepository.updateExerciseLogDuration(ex.logId, duration)
            }

            _sessionState.update { s ->
                s?.copy(
                    activeExerciseIndex = -1,
                    exercises = s.exercises.mapIndexed { i, e ->
                        if (i == currentIdx) e.copy(isChronoRunning = false) else e
                    }
                )
            }
        }
    }

    fun nextExercise() {
        val state = _sessionState.value ?: return
        val current = state.activeExerciseIndex
        val nextIdx = if (current >= 0) {
            (current + 1).coerceAtMost(state.exercises.size - 1)
        } else {
            state.exercises.indexOfFirst { !it.isCompleted }.coerceAtLeast(0)
        }
        if (nextIdx in state.exercises.indices) {
            toggleExerciseChrono(nextIdx)
        }
    }

    // ──────────────────────────────────────────────
    // REST TIMER
    // ──────────────────────────────────────────────

    fun startRestTimer(exerciseIndex: Int? = null, seconds: Int = 90) {
        val state = _sessionState.value ?: return
        val targetIdx = exerciseIndex ?: if (state.activeExerciseIndex >= 0) state.activeExerciseIndex else {
            state.exercises.indexOfFirst { !it.isCompleted }.coerceAtLeast(0)
        }

        restTimerJob?.cancel()
        activeRestExerciseIndex = targetIdx
        val totalMs = seconds * 1000L
        restTimerTargetEndTime = SystemClock.elapsedRealtime() + totalMs

        _sessionState.update { s ->
            s?.copy(
                exercises = s.exercises.mapIndexed { i, ex ->
                    if (i == targetIdx) {
                        ex.copy(
                            restTimerTotalSeconds = seconds,
                            restTimerRemainingMs = totalMs,
                            isRestTimerRunning = true,
                            isRestTimerFinished = false
                        )
                    } else {
                        ex.copy(isRestTimerRunning = false, isRestTimerFinished = false)
                    }
                }
            )
        }

        restTimerJob = managerScope.launch {
            while (isActive) {
                val remaining = restTimerTargetEndTime - SystemClock.elapsedRealtime()
                if (remaining <= 0L) {
                    _sessionState.update { s ->
                        s?.copy(
                            exercises = s.exercises.mapIndexed { i, ex ->
                                if (i == targetIdx) ex.copy(
                                    restTimerRemainingMs = 0L,
                                    isRestTimerRunning = false,
                                    isRestTimerFinished = true
                                ) else ex
                            }
                        )
                    }
                    triggerAlarmFeedback()
                    break
                }
                _sessionState.update { s ->
                    s?.copy(
                        exercises = s.exercises.mapIndexed { i, ex ->
                            if (i == targetIdx) ex.copy(restTimerRemainingMs = remaining) else ex
                        }
                    )
                }
                delay(100)
            }
        }
    }

    fun cancelRestTimer(exerciseIndex: Int? = null) {
        val target = exerciseIndex ?: activeRestExerciseIndex
        if (target == activeRestExerciseIndex) {
            restTimerJob?.cancel()
            restTimerJob = null
            activeRestExerciseIndex = -1
        }
        _sessionState.update { s ->
            s?.copy(
                exercises = s.exercises.mapIndexed { i, ex ->
                    if (i == target) {
                        ex.copy(
                            isRestTimerRunning = false,
                            isRestTimerFinished = false,
                            restTimerRemainingMs = 0L
                        )
                    } else ex
                }
            )
        }
    }

    fun toggleExerciseDone(exerciseIndex: Int) {
        val state = _sessionState.value ?: return
        if (exerciseIndex !in state.exercises.indices) return

        val exercise = state.exercises[exerciseIndex]
        val newDone = !exercise.isCompleted

        if (state.activeExerciseIndex == exerciseIndex) {
            pauseExerciseChrono()
        }

        _sessionState.update { s ->
            s?.copy(
                exercises = s.exercises.mapIndexed { i, ex ->
                    if (i == exerciseIndex) ex.copy(isCompleted = newDone) else ex
                }
            )
        }

        managerScope.launch {
            sessionRepository.markExerciseCompleted(exercise.logId, newDone)
        }
    }

    // ──────────────────────────────────────────────
    // SESSION COUNTDOWN TIMER
    // ──────────────────────────────────────────────

    fun setSessionTimer(totalMinutes: Int) {
        val totalSeconds = totalMinutes * 60
        if (totalSeconds <= 0) return

        sessionTimerJob?.cancel()
        val durationMs = totalSeconds * 1000L
        sessionTimerTargetEndTime = SystemClock.elapsedRealtime() + durationMs

        _sessionState.update {
            it?.copy(
                sessionTimerTotalSeconds = totalSeconds,
                sessionTimerRemainingMs = durationMs,
                isSessionTimerRunning = true,
                isSessionTimerFinished = false
            )
        }

        sessionTimerJob = managerScope.launch {
            while (isActive) {
                val remaining = sessionTimerTargetEndTime - SystemClock.elapsedRealtime()
                if (remaining <= 0L) {
                    _sessionState.update {
                        it?.copy(
                            sessionTimerRemainingMs = 0L,
                            isSessionTimerRunning = false,
                            isSessionTimerFinished = true
                        )
                    }
                    triggerAlarmFeedback()
                    break
                }
                _sessionState.update { it?.copy(sessionTimerRemainingMs = remaining) }
                delay(200)
            }
        }
    }

    fun cancelSessionTimer() {
        sessionTimerJob?.cancel()
        sessionTimerJob = null
        _sessionState.update {
            it?.copy(
                sessionTimerTotalSeconds = null,
                sessionTimerRemainingMs = 0L,
                isSessionTimerRunning = false,
                isSessionTimerFinished = false
            )
        }
    }

    // ──────────────────────────────────────────────
    // FINISH SESSION
    // ──────────────────────────────────────────────

    fun finishSession(onFinished: (() -> Unit)? = null) {
        val state = _sessionState.value ?: return

        sessionChronoJob?.cancel()
        exerciseChronoJob?.cancel()
        sessionTimerJob?.cancel()
        restTimerJob?.cancel()

        val totalMs = state.sessionElapsedMs
        val sId = state.sessionId

        managerScope.launch {
            state.exercises.forEach { ex ->
                sessionRepository.updateExerciseLogDuration(ex.logId, ex.chronoElapsedMs)
                sessionRepository.markExerciseCompleted(ex.logId, ex.isCompleted)
            }
            if (sId > 0) {
                sessionRepository.finishSession(sId, totalMs)
            }
            _sessionState.value = null
            stopForegroundService()
            onFinished?.invoke()
        }
    }

    fun pauseSession(onPaused: (() -> Unit)? = null) {
        val state = _sessionState.value ?: return

        sessionChronoJob?.cancel()
        exerciseChronoJob?.cancel()
        sessionTimerJob?.cancel()
        restTimerJob?.cancel()

        managerScope.launch {
            state.exercises.forEach { ex ->
                sessionRepository.updateExerciseLogDuration(ex.logId, ex.chronoElapsedMs)
                sessionRepository.markExerciseCompleted(ex.logId, ex.isCompleted)
            }
            // Keep end_time as null in database so it is not finished
            _sessionState.value = null
            stopForegroundService()
            onPaused?.invoke()
        }
    }

    private fun triggerAlarmFeedback() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val mgr = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                mgr?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 150, 300, 150, 300), -1))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(longArrayOf(0, 300, 150, 300, 150, 300), -1)
                }
            }

            val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, alertUri)
            ringtone?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
