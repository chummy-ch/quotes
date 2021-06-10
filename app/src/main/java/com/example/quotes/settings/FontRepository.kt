package com.example.quotes.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.quotes.R
import com.example.quotes.onIO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FontRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        val font_key = intPreferencesKey("font_key")
        const val DEFAULT_FONT = R.font.roboto
    }

    private val fonts = mutableListOf<FontModel>()

    suspend fun getFonts(): List<FontModel> {
        if (fonts.isNullOrEmpty()) fillFonts()
        fillSavedFont()
        return fonts.toList()
    }

    fun getFont() = dataStore.data.map { preference ->
        preference.getFont()
    }

    suspend fun saveFont(fontRes: Int): List<FontModel> {
        onIO {
            dataStore.edit { font ->
                font[font_key] = fontRes
            }
        }
        updateFontList()
        fillSavedFont()
        return fonts.toList()
    }

    suspend fun getFontSuspend(): Int = dataStore.data.first().getFont()

    private fun updateFontList() {
        val newList = fonts.map { font ->
            if (font.isSelected) font.copy(isSelected = false)
            else font
        }
        fonts.clear()
        fonts.addAll(newList)
    }

    private suspend fun fillSavedFont() {
        val saveFont = getFontSuspend()
        val newList = fonts.map { font ->
            if (font.res == saveFont) font.copy(isSelected = true)
            else font
        }
        fonts.clear()
        fonts.addAll(newList)
    }

    private fun Preferences.getFont() = this[font_key] ?: DEFAULT_FONT

    private fun fillFonts() {
        fonts.addAll(
            listOf(
                FontModel("Roboto", R.font.roboto, false),
                FontModel("Circular Std", R.font.circularstd_black, false),
                FontModel("Arkipilago", R.font.arkipelago, false),
                FontModel("Wetware Cyrillic", R.font.wetware_cyrillic, false),
                FontModel("Garage", R.font.garage, false)
            )
        )
    }
}
