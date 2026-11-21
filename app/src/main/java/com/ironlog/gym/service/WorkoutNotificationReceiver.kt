package com.ironlog.gym.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ironlog.gym.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WorkoutNotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sessionManager: WorkoutSessionManager

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_TOGGLE_EXERCISE_CHRONO -> {
                sessionManager.toggleExerciseChrono()
            }
            ACTION_NEXT_EXERCISE -> {
                sessionManager.nextExercise()
            }
            ACTION_REST_TIMER -> {
                sessionManager.startRestTimer(seconds = 90)
            }
            ACTION_FINISH_SESSION -> {
                // Open app to prompt finish dialog or finish
                val openIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("navigate_to", "session")
                    putExtra("show_finish_dialog", true)
                }
                context.startActivity(openIntent)
            }
        }
    }

    companion object {
        const val ACTION_TOGGLE_EXERCISE_CHRONO = "com.ironlog.gym.ACTION_TOGGLE_EXERCISE_CHRONO"
        const val ACTION_NEXT_EXERCISE = "com.ironlog.gym.ACTION_NEXT_EXERCISE"
        const val ACTION_REST_TIMER = "com.ironlog.gym.ACTION_REST_TIMER"
        const val ACTION_FINISH_SESSION = "com.ironlog.gym.ACTION_FINISH_SESSION"
    }
}
