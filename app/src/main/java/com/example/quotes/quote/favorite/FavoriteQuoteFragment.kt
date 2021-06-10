package com.example.quotes.quote.favorite

import android.os.Bundle
import android.view.View
import coil.load
import com.airbnb.epoxy.VisibilityState
import com.airbnb.mvrx.fragmentViewModel
import com.example.quotes.R
import com.example.quotes.epoxy.simpleController
import com.example.quotes.epoxy.viewholders.quoteViewHolder
import com.example.quotes.quote.QuoteBaseListFragment
import com.example.quotes.settings.FontRepository
import com.google.android.material.snackbar.Snackbar

class FavoriteQuoteFragment : QuoteBaseListFragment(R.layout.fragment_quote_list) {

    private val viewModel: FavoriteQuoteViewModel by fragmentViewModel()

    override fun epoxyController() = simpleController(viewModel) { state ->
        val quotes = state.quotes.invoke()
        val theme = state.theme.invoke()
        val font = state.font.invoke() ?: FontRepository.DEFAULT_FONT

        val typeface = resources.getFont(font)
        if (theme != null) {
            quotes?.forEach { quoteModel ->
                quoteViewHolder {
                    id(quoteModel.id)
                    quote(quoteModel)
                    font(typeface)
                    onVisibilityStateChanged { model, view, visibilityState ->
                        if (visibilityState == VisibilityState.FOCUSED_VISIBLE) {
                            bindButtonBackground(quoteModel.isFavorite)
                            viewModel.addToViewed(quoteModel.id)
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quoteBaseViewModel = viewModel

        with(viewModel) {
            onAsync(FavoriteQuoteState::error, onFail = {
                Snackbar.make(view, it.toString(), 2000).show()
            })
            onAsync(FavoriteQuoteState::theme, onSuccess = { theme ->
                binding.themeImageView.load(theme)
            })
            onAsync(FavoriteQuoteState::selectedQuote, onSuccess = { quote ->
                bindButtonBackground(quote.isFavorite)
            })
        }

    }
}
