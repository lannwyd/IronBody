package com.ironlog.gym.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ironlog.gym.MainActivity
import com.ironlog.gym.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class WorkoutSessionService : Service() {

    @Inject
    lateinit var sessionManager: WorkoutSessionManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var notificationTickerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopWorkoutService()
                return START_NOT_STICKY
            }
            else -> {
                startForegroundNotification()
                startTicker()
            }
        }
        return START_STICKY
    }

    private fun startForegroundNotification() {
        val initialNotification = buildNotification()
        startForeground(NOTIFICATION_ID, initialNotification)
    }

    private fun startTicker() {
        notificationTickerJob?.cancel()
        notificationTickerJob = serviceScope.launch {
            while (isActive) {
                val state = sessionManager.sessionState.value
                if (state == null) {
                    stopWorkoutService()
                    break
                }
                updateNotification(state)
                delay(1000)
            }
        }
    }

    private fun updateNotification(state: WorkoutSessionState) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val notification = buildNotification(state)
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(state: WorkoutSessionState? = sessionManager.sessionState.value): Notification {
        val dayName = state?.dayName?.ifBlank { "Workout" } ?: "Workout"
        val sessionDuration = formatDuration(state?.sessionElapsedMs ?: 0L)

        val activeExercise = state?.activeExercise
        val activeRest = state?.activeRestExercise

        val contentText = when {
            activeRest != null && (activeRest.isRestTimerRunning || activeRest.isRestTimerFinished) -> {
                if (activeRest.isRestTimerFinished) "⏳ Rest period complete!"
                else "⏳ Rest: ${formatCountdown(activeRest.restTimerRemainingMs)} remaining"
            }
            activeExercise != null -> {
                val status = if (activeExercise.isChronoRunning) "▶" else "⏸"
                "$status ${activeExercise.exerciseName}: ${formatDuration(activeExercise.chronoElapsedMs)} (${activeExercise.exerciseWeight} ${activeExercise.exerciseUnit})"
            }
            else -> {
                val doneCount = state?.exercises?.count { it.isCompleted } ?: 0
                val totalCount = state?.exercises?.size ?: 0
                "$dayName • $doneCount/$totalCount completed"
            }
        }

        // Content intent: open MainActivity directly to Screen.Session
        val contentIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "session")
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            101,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Toggle Active Exercise Chronometer (Play/Pause)
        val isRunning = activeExercise?.isChronoRunning == true
        val toggleActionIntent = Intent(this, WorkoutNotificationReceiver::class.java).apply {
            action = WorkoutNotificationReceiver.ACTION_TOGGLE_EXERCISE_CHRONO
        }
        val togglePendingIntent = PendingIntent.getBroadcast(
            this,
            102,
            toggleActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val toggleActionTitle = if (isRunning) getString(R.string.notif_action_pause) else getString(R.string.notif_action_resume)

        // Action: Next Exercise
        val nextActionIntent = Intent(this, WorkoutNotificationReceiver::class.java).apply {
            action = WorkoutNotificationReceiver.ACTION_NEXT_EXERCISE
        }
        val nextPendingIntent = PendingIntent.getBroadcast(
            this,
            103,
            nextActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Rest Timer (90s)
        val restActionIntent = Intent(this, WorkoutNotificationReceiver::class.java).apply {
            action = WorkoutNotificationReceiver.ACTION_REST_TIMER
        }
        val restPendingIntent = PendingIntent.getBroadcast(
            this,
            104,
            restActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Finish Session
        val finishActionIntent = Intent(this, WorkoutNotificationReceiver::class.java).apply {
            action = WorkoutNotificationReceiver.ACTION_FINISH_SESSION
        }
        val finishPendingIntent = PendingIntent.getBroadcast(
            this,
            105,
            finishActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle("$dayName • $sessionDuration")
            .setContentText(contentText)
            .setContentIntent(contentPendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(
                if (isRunning) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                toggleActionTitle,
                togglePendingIntent
            )
            .addAction(
                android.R.drawable.ic_media_next,
                getString(R.string.notif_action_next),
                nextPendingIntent
            )
            .addAction(
                android.R.drawable.ic_lock_idle_alarm,
                getString(R.string.notif_action_rest),
                restPendingIntent
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                getString(R.string.notif_action_finish),
                finishPendingIntent
            )

        return builder.build()
    }

    private fun stopWorkoutService() {
        notificationTickerJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopWorkoutService()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Workout Session Tracker"
            val descriptionText = "Displays real-time workout chronometer and interactive controls"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun formatDuration(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }

    private fun formatCountdown(ms: Long): String {
        val totalSeconds = (ms + 999) / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    companion object {
        const val CHANNEL_ID = "ironbody_workout_session_channel"
        const val NOTIFICATION_ID = 3001

        const val ACTION_START_OR_UPDATE = "com.ironlog.gym.service.ACTION_START_OR_UPDATE"
        const val ACTION_STOP = "com.ironlog.gym.service.ACTION_STOP"
    }
}
