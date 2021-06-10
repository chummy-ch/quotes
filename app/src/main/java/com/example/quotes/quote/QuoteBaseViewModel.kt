package com.example.quotes.quote

import com.example.quotes.settings.FontRepository
import com.example.quotes.theme.ThemeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface QuoteBaseViewModel {
    val quoteRepository: QuoteRepository
    val fontRepository: FontRepository
    val themeRepository: ThemeRepository
    val scope: CoroutineScope

    suspend fun getCurrentTheme() = themeRepository.getStringTheme()

    fun getById(quoteId: Long) = quoteRepository.getById(quoteId)

    fun addToFavorite(quoteId: Long) {
        scope.launch {
            val quote = quoteRepository.addToFavorite(quoteId)
            updateQuote(quote)
        }
    }

    fun removeFromFavorite(quoteId: Long) {
        scope.launch {
            val quote = quoteRepository.removeFromFavorite(quoteId)
            updateQuote(quote)
        }
    }

    suspend fun getFont() = fontRepository.getFontSuspend()

    fun updateQuote(quote: QuoteModel)

    fun addToViewed(id: Long)
}
