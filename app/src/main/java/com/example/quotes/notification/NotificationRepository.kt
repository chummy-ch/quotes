package com.example.quotes.notification

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.quotes.onIO
import kotlinx.coroutines.flow.first

class NotificationRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val NOTIFY_KEY = intPreferencesKey("notification_key")
    }

    private var selectedNotification: Int = 0;
    private var notifications = mutableListOf<Notification>()

    fun getNotificationList(): List<Notification> {
        if (notifications.isNullOrEmpty()) fillNotifications()
        return notifications.toList()
    }

    suspend fun saveSelectedNotification(id: Int): Int {
        onIO {
            selectedNotification = id
            dataStore.edit { notify ->
                notify[NOTIFY_KEY] = selectedNotification
            }
        }
        return selectedNotification
    }

    suspend fun loadNotificationPeriod(): Int {
        selectedNotification = dataStore.data.first().getNotification()
        return selectedNotification
    }

    private suspend fun fillSelectedNotification() {
        val id = loadNotificationPeriod()
        notifications = notifications.map { notify ->
            if (id == notify.id) getNotificationFromSelected()
            else notify
        }.toMutableList()
    }

    private fun getNotificationFromSelected(): Notification {
        if (notifications.isNullOrEmpty()) fillNotifications()
        return notifications.first { notify ->
            notify.id == selectedNotification
        }
    }

    private fun Preferences.getNotification() = this[NOTIFY_KEY] ?: 0

    private fun fillNotifications() {
        notifications.addAll(
            listOf(Daily(), Never())
        )
    }
}
