package com.example.quotes.theme

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.airbnb.mvrx.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@RequiresApi(Build.VERSION_CODES.O)
class ThemeViewModel(
    initialState: ThemeState,
    private val themeRepository: ThemeRepository
) : MavericksViewModel<ThemeState>(initialState) {
    init {
        viewModelScope.launch {
            setState {
                copy(themes = Loading())
            }
            val themeList = themeRepository.getThemesList()
            setState {
                copy(themes = Success(themeList))
            }

            val themeColor = themeRepository.getStringTheme()
            setState { copy(currentTheme = Success(themeColor.toUri())) }
        }
    }

    fun onQuoteThemeSelected(theme: Uri) {
        viewModelScope.launch {
            themeRepository.saveTheme(theme)
        }
    }

    companion object : MavericksViewModelFactory<ThemeViewModel, ThemeState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: ThemeState
        ): ThemeViewModel {
            val themeRepository: ThemeRepository by viewModelContext.activity.inject()
            return ThemeViewModel(state, themeRepository)
        }
    }
}
