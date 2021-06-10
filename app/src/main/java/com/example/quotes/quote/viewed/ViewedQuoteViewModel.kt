package com.example.quotes.quote.viewed

import androidx.core.net.toUri
import com.airbnb.mvrx.*
import com.example.quotes.quote.QuoteBaseViewModel
import com.example.quotes.quote.QuoteModel
import com.example.quotes.quote.QuoteRepository
import com.example.quotes.settings.FontRepository
import com.example.quotes.theme.ThemeRepository
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ViewedQuoteViewModel(
    private val initialState: ViewedQuoteState,
    override val quoteRepository: QuoteRepository,
    override val fontRepository: FontRepository,
    override val themeRepository: ThemeRepository
) : MavericksViewModel<ViewedQuoteState>(initialState), QuoteBaseViewModel {

    init {
        setState { copy(quotes = Loading(), font = Loading(), theme = Loading(), selectedQuote = Loading()) }

        viewModelScope.launch {
            val quotes = quoteRepository.getViewedQuotes()
            setState { copy(quotes = Success(quotes)) }

            val theme = themeRepository.getStringTheme()
            setState { copy(theme = Success(theme.toUri())) }

            val font = fontRepository.getFontSuspend()
            setState { copy(font = Success(font)) }
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

    // TODO: 24.04.21 Remove unused fun
    override fun addToViewed(id: Long) = Unit

    companion object : MavericksViewModelFactory<ViewedQuoteViewModel, ViewedQuoteState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: ViewedQuoteState
        ): ViewedQuoteViewModel {
            val quoteRepository: QuoteRepository by viewModelContext.activity.inject()
            val themeRepository: ThemeRepository by viewModelContext.activity.inject()
            val fontRepository: FontRepository by viewModelContext.activity.inject()
            return ViewedQuoteViewModel(state, quoteRepository, fontRepository, themeRepository)
        }
    }
}
