package com.example.quotes.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.quotes.MainActivity
import com.example.quotes.MainActivity.Companion.channelId
import com.example.quotes.MainActivity.Companion.quoteIdExtra
import com.example.quotes.R
import com.example.quotes.quote.QuoteRepository
import org.koin.java.KoinJavaComponent.inject

class QuoteNotifyWork(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    private suspend fun sendNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val quoteRepository: QuoteRepository by inject(QuoteRepository::class.java)
        val list = quoteRepository.getQuotesListSuspend()
        val quote = list.firstOrNull { !it.isViewed } ?: list.first()
        intent.putExtra(quoteIdExtra, quote)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_heart_in)
            .setContentTitle(context.getString(R.string.notify_quote_title))
            .setContentText(quote.text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }
}
