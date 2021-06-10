package com.example.quotes.theme

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.quotes.onIO
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ThemeRepository(private val dataStore: DataStore<Preferences>) {
    private var themeList = mutableListOf<StorageReference>()

    suspend fun getThemesList(): List<StorageReference> {
        if (themeList.isEmpty()) {
            themeList = getUriList()
        }
        return themeList.toList()
    }

    private suspend fun getUriList() = suspendCancellableCoroutine<MutableList<StorageReference>> { con ->
        val storage = Firebase.storage
        val listRef = storage.reference.child("/")
        listRef.listAll().addOnSuccessListener { res ->
            con.resume(res.items)
        }
    }

    fun getThemeColor(): Flow<String> = dataStore.data
        .map { preferences ->
            preferences.selectedThemeColor()
        }


    suspend fun saveTheme(uri: Uri) {
        onIO {
            dataStore.edit { theme ->
                theme[theme_key] = uri.toString()
            }
        }
    }

    suspend fun getStringTheme(): String = dataStore.data.first().selectedThemeColor()

    private fun Preferences.selectedThemeColor() = this[theme_key] ?: DEFAULT_THEME

    companion object {
        val theme_key = stringPreferencesKey("string_theme_key")
        const val DEFAULT_THEME =
            "https://firebasestorage.googleapis.com/v0/b/quotes-d716f.appspot.com/o/background_16_1080x2340.webp?alt=media&token=7520ff52-d18b-42ab-be8a-94386787025b"
    }
}
