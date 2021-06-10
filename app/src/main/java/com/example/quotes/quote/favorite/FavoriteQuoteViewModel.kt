package com.example.quotes.quote.favorite

import androidx.core.net.toUri
import com.airbnb.mvrx.*
import com.example.quotes.quote.QuoteBaseViewModel
import com.example.quotes.quote.QuoteModel
import com.example.quotes.quote.QuoteRepository
import com.example.quotes.quote.QuoteViewModel
import com.example.quotes.settings.FontRepository
import com.example.quotes.theme.ThemeRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FavoriteQuoteViewModel(
    initialState: FavoriteQuoteState,
    override val quoteRepository: QuoteRepository,
    override val fontRepository: FontRepository,
    override val themeRepository: ThemeRepository,
    val error: Async<QuoteViewModel.ErrorEvent> = Uninitialized
) : MavericksViewModel<FavoriteQuoteState>(initialState), QuoteBaseViewModel {

    init {
        viewModelScope.launch {
            setState { copy(quotes = Loading(), selectedQuote = Loading()) }

            val quotesList = quoteRepository.getFavoriteQuoteList()
            setState { copy(quotes = Success(quotesList)) }

            val font = fontRepository.getFontSuspend()
            setState { copy(font = Success(font)) }

            themeRepository.getThemeColor().collect { color ->
                setState {
                    copy(theme = Success(color.toUri()))
                }
            }
        }
    }

    override val scope = viewModelScope

    override fun updateQuote(quote: QuoteModel) {
        setState {
            val quotes = this.quotes() ?: return@setState copy()
            val quotesUpdated = quotes.map { if (it.id == quote.id) quote else it }

            copy(quotes = Success(quotesUpdated), selectedQuote = Success(quote))
        }
    }

    override fun addToViewed(id: Long) {
        viewModelScope.launch {
            quoteRepository.saveViewedIds(id)
        }
    }

    companion object : MavericksViewModelFactory<FavoriteQuoteViewModel, FavoriteQuoteState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: FavoriteQuoteState
        ): FavoriteQuoteViewModel {
            val quoteRepository: QuoteRepository by viewModelContext.activity.inject()
            val themeRepository: ThemeRepository by viewModelContext.activity.inject()
            val fontRepository: FontRepository by viewModelContext.activity.inject()
            return FavoriteQuoteViewModel(state, quoteRepository, fontRepository, themeRepository)
        }
    }
}
