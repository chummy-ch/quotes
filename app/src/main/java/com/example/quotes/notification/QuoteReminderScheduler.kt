package com.example.quotes.notification

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkRequest
import java.util.*
import java.util.concurrent.TimeUnit

class QuoteReminderScheduler(private val time: Long) {
    companion object {
        const val ALARM_QUOTE = "alarm_quote"
    }

    fun createOneTimeWorkRequest(alarmNum: Int): WorkRequest {
        val dueDate = Calendar.getInstance()
        val currentDate = Calendar.getInstance()
        val alarm = getAlarmHM()
        with(dueDate) {
            set(Calendar.HOUR_OF_DAY, alarm.first.toInt())
            set(Calendar.MINUTE, alarm.second.toInt())
            if (before(currentDate)) {
                add(Calendar.HOUR_OF_DAY, 24)
            }
            add(Calendar.HOUR_OF_DAY, 24 * (alarmNum - 1))
        }
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        return OneTimeWorkRequest.Builder(QuoteNotifyWork::class.java)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .addTag(ALARM_QUOTE)
            .build()
    }

    private fun getAlarmHM(): Pair<Long, Long> {
        var minutes = time
        val hours = minutes / 60
        minutes -= hours * 60
        return Pair(hours, minutes)
    }
}
