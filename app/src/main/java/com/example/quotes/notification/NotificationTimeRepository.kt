package com.example.quotes.notification

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.example.quotes.onIO
import kotlinx.coroutines.flow.first

class NotificationTimeRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val ALARM_KEY = longPreferencesKey("alarm_key")
        const val DEFAULT_ALARM: Long = 540
    }

    private var alarms = mutableListOf<Long>()

    suspend fun loadAlarms(): List<Long> {
        onIO {
            val alarm = dataStore.data.first().loadAlarms()
            alarms = mutableListOf(alarm)
        }
        return alarms.toList()
    }

    suspend fun saveAlarms(alarm: Long): List<Long> {
        onIO {
            dataStore.edit { preference ->
                preference[ALARM_KEY] = alarm
            }
        }
        alarms = mutableListOf(alarm)
        return alarms.toList()
    }

    private fun Preferences.loadAlarms() = this[ALARM_KEY] ?: DEFAULT_ALARM
}