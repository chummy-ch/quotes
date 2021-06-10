package com.example.quotes.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.example.quotes.BootReceiver
import com.example.quotes.MainActivity
import com.example.quotes.QuoteApp
import com.example.quotes.R
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class NotificationUsecase(private val workManager: WorkManager, private val context: Context) {
    init {
        createNotificationChannel()
    }

    suspend fun notifyQuote() {
        workManager.cancelAllWorkByTag(QuoteReminderScheduler.ALARM_QUOTE)
        if (!canNotify()) return

        val notificationRepository: NotificationTimeRepository by (context as QuoteApp).inject()
        val timeList = notificationRepository.loadAlarms()
        timeList.forEach { time ->
            for (alarmNum in 1..7) {
                val dailyWorkRequest = QuoteReminderScheduler(time).createOneTimeWorkRequest(alarmNum)
                workManager.enqueue(dailyWorkRequest)
            }
        }
    }

    fun switchBootReceiver(status: Boolean) {
        val receiver = ComponentName(context, BootReceiver::class.java)
        val enableStatus = when (status) {
            true -> PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            false -> PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        context.packageManager.setComponentEnabledSetting(
            receiver,
            enableStatus,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = context.getString(R.string.notify_quote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                MainActivity.channelId,
                MainActivity.channelId,
                importance
            )
                .apply {
                    description = descriptionText
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private suspend fun canNotify(): Boolean {
        val notificationPeriodRep by inject(NotificationRepository::class.java)
        val period = notificationPeriodRep.loadNotificationPeriod()
        if (period == 1) return false
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = manager.getNotificationChannel(MainActivity.channelId) ?: return true
            if (channel.importance == NotificationManager.IMPORTANCE_NONE) return false
        }
        return true
    }
}