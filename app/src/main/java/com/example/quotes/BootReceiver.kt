package com.example.quotes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.quotes.notification.NotificationUsecase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            CoroutineScope(Dispatchers.IO).launch {
                val notificationUsecase: NotificationUsecase by inject(NotificationUsecase::class.java)
                notificationUsecase.notifyQuote()
            }
        }
    }
}
