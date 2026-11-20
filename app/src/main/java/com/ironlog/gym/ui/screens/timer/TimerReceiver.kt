package com.ironlog.gym.ui.screens.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        TimerNotificationHelper.showTimerFinishedNotification(context)
    }
}
