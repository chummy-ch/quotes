package com.example.quotes.quote

import androidx.core.net.toUri
import com.airbnb.mvrx.*
import com.example.quotes.category.CategoriesRepository
import com.example.quotes.notification.NotificationUsecase
import com.example.quotes.settings.FontRepository
import com.example.quotes.theme.ThemeRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject


class QuoteViewModel(
    initialState: QuoteState,
    override val quoteRepository: QuoteRepository,
    private val categoriesRepository: CategoriesRepository,
    override val fontRepository: FontRepository,
    override val themeRepository: ThemeRepository,
    val error: Async<ErrorEvent> = Uninitialized
) : MavericksViewModel<QuoteState>(initialState), QuoteBaseViewModel {

    init {
        setState { copy(quotes = Loading(), selectedQuote = Loading()) }

        viewModelScope.launch {
            val notificationUsecase by inject(NotificationUsecase::class.java)
            notificationUsecase.notifyQuote()
        }

        viewModelScope.launch {
            val quotesList = quoteRepository.getQuotesListSuspend()
            categoriesRepository.getSelectedCategoriesFlow().collect { categoryList ->
                val quoteFilteredList = quotesList.filter { quote ->
                    categoryList.any { category ->
                        quote.categoryId == category.id
                    }
                }
                setState { copy(quotes = Success(quoteFilteredList)) }
            }
        }

        viewModelScope.launch {

            themeRepository.getThemeColor().collect { uri ->
                setState {
                    copy(theme = Success(uri.toUri()))
                }
            }
        }
        viewModelScope.launch {
            fontRepository.getFont().collect { font ->
                setState { copy(font = Success(font)) }
            }
        }
    }

    override val scope = viewModelScope

    enum class ErrorEvent {
        NETWORK_ERROR
    }

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

    companion object : MavericksViewModelFactory<QuoteViewModel, QuoteState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: QuoteState
        ): QuoteViewModel {
            with(viewModelContext.activity) {
                val quoteRepository: QuoteRepository by inject()
                val categoriesRepository: CategoriesRepository by inject()
                val themeRepository: ThemeRepository by inject()
                val fontRepository: FontRepository by inject()
                return QuoteViewModel(state, quoteRepository, categoriesRepository, fontRepository, themeRepository)
            }
        }
    }
}
