package com.example.quotes

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.flow.first

class ConfigRepository(private val remoteConfig: FirebaseRemoteConfig, private val dataStore: DataStore<Preferences>) {
    companion object {
        const val AD_KEY = "adviews_per_category"
        private val adDataStoreKey = longPreferencesKey("views_times")
    }

    private val adRequiredViews by lazy { getRequiredViews() }

    private fun getRequiredViews() = remoteConfig.getLong(AD_KEY)

    suspend fun unlockCategory(): Boolean {
        val adViews = getAdViews()
        if (adViews >= adRequiredViews) {
            clearViews(adViews)
            return true
        }
        return false
    }

    private suspend fun clearViews(adViews: Long) {
        onIO {
            dataStore.edit { it[adDataStoreKey] = adViews - adRequiredViews }
        }
    }

    suspend fun addViewedTime() {
        onIO {
            dataStore.edit { views ->
                val currentVies = getAdViews()
                views[adDataStoreKey] = currentVies + 1
            }
        }
    }

    private suspend fun getAdViews(): Long = dataStore.data.first().viewedTimes()

    private fun Preferences.viewedTimes() =
        this[adDataStoreKey] ?: 0
}
