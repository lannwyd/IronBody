package com.ironlog.gym

import android.app.Application
import com.ironlog.gym.ui.screens.timer.TimerNotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class IronLogApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TimerNotificationHelper.createNotificationChannel(this)
    }
}
