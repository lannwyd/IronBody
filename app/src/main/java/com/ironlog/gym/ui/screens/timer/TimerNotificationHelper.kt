package com.ironlog.gym.ui.screens.timer

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ironlog.gym.MainActivity
import com.ironlog.gym.R

object TimerNotificationHelper {
    const val CHANNEL_ID = "ironbody_timer_channel"
    const val CHRONO_CHANNEL_ID = "ironbody_chronometer_channel"
    const val NOTIFICATION_ID = 2001
    const val CHRONO_NOTIFICATION_ID = 2002

    const val ACTION_CHRONO_START = "com.ironlog.gym.ACTION_CHRONO_START"
    const val ACTION_CHRONO_PAUSE = "com.ironlog.gym.ACTION_CHRONO_PAUSE"
    const val ACTION_CHRONO_LAP = "com.ironlog.gym.ACTION_CHRONO_LAP"
    const val ACTION_CHRONO_RESET = "com.ironlog.gym.ACTION_CHRONO_RESET"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Timer Alarm channel
            val name = context.getString(R.string.notification_channel_timer_name)
            val descriptionText = context.getString(R.string.notification_channel_timer_desc)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                enableLights(true)
                lightColor = android.graphics.Color.GREEN
            }
            notificationManager.createNotificationChannel(channel)

            // Chronometer channel (low importance so it doesn't chime on each update)
            val chronoChannel = NotificationChannel(
                CHRONO_CHANNEL_ID,
                "Chronometer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Ongoing stopwatch notification"
                setShowBadge(false)
                enableVibration(false)
                enableLights(false)
            }
            notificationManager.createNotificationChannel(chronoChannel)
        }
    }

    fun showTimerFinishedNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "timer")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle(context.getString(R.string.notification_timer_title))
            .setContentText(context.getString(R.string.notification_timer_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                notificationManager.notify(NOTIFICATION_ID, notification)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun scheduleAlarm(context: Context, triggerAtMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, TimerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            try {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, TimerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun showChronometerNotification(
        context: Context,
        isRunning: Boolean,
        elapsedMs: Long,
        lapsCount: Int = 0
    ) {
        createNotificationChannel(context)

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "timer")
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            101,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIntent = Intent(context, TimerReceiver::class.java).apply { action = ACTION_CHRONO_PAUSE }
        val pausePendingIntent = PendingIntent.getBroadcast(
            context, 102, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val startIntent = Intent(context, TimerReceiver::class.java).apply { action = ACTION_CHRONO_START }
        val startPendingIntent = PendingIntent.getBroadcast(
            context, 103, startIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val lapIntent = Intent(context, TimerReceiver::class.java).apply { action = ACTION_CHRONO_LAP }
        val lapPendingIntent = PendingIntent.getBroadcast(
            context, 104, lapIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val resetIntent = Intent(context, TimerReceiver::class.java).apply { action = ACTION_CHRONO_RESET }
        val resetPendingIntent = PendingIntent.getBroadcast(
            context, 105, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHRONO_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_logo)
            .setContentIntent(contentPendingIntent)
            .setOnlyAlertOnce(true)
            .setShowWhen(true)
            .setCategory(NotificationCompat.CATEGORY_STOPWATCH)

        if (isRunning) {
            val lapText = if (lapsCount > 0) " • Lap $lapsCount" else ""
            builder.setContentTitle("Chronometer")
                .setContentText("Running$lapText")
                .setOngoing(true)
                .setUsesChronometer(true)
                .setWhen(System.currentTimeMillis() - elapsedMs)
                .addAction(android.R.drawable.ic_media_pause, "Pause", pausePendingIntent)
                .addAction(android.R.drawable.ic_menu_add, "Lap", lapPendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Reset", resetPendingIntent)
        } else {
            val totalSeconds = elapsedMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val formatted = String.format("%02d:%02d", minutes, seconds)
            val lapText = if (lapsCount > 0) " • $lapsCount laps" else ""
            builder.setContentTitle("Chronometer (Paused)")
                .setContentText("$formatted$lapText")
                .setOngoing(false)
                .setUsesChronometer(false)
                .addAction(android.R.drawable.ic_media_play, "Resume", startPendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Reset", resetPendingIntent)
        }

        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                notificationManager.notify(CHRONO_NOTIFICATION_ID, builder.build())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun cancelChronometerNotification(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        try {
            notificationManager.cancel(CHRONO_NOTIFICATION_ID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
