package com.ironlog.gym.ui.screens.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimerReceiver : BroadcastReceiver() {

    @Inject
    lateinit var chronometerManager: ChronometerManager

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            TimerNotificationHelper.ACTION_CHRONO_START -> {
                chronometerManager.start()
            }
            TimerNotificationHelper.ACTION_CHRONO_PAUSE -> {
                chronometerManager.pause()
            }
            TimerNotificationHelper.ACTION_CHRONO_LAP -> {
                chronometerManager.lap()
            }
            TimerNotificationHelper.ACTION_CHRONO_RESET -> {
                chronometerManager.reset()
            }
            else -> {
                TimerNotificationHelper.showTimerFinishedNotification(context)
            }
        }
    }
}
