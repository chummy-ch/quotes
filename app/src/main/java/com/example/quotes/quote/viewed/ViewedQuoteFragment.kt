package com.example.quotes.quote.viewed

import android.os.Bundle
import android.view.View
import coil.load
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.VisibilityState
import com.airbnb.mvrx.fragmentViewModel
import com.example.quotes.R
import com.example.quotes.epoxy.simpleController
import com.example.quotes.epoxy.viewholders.quoteViewHolder
import com.example.quotes.quote.QuoteBaseListFragment
import com.example.quotes.settings.FontRepository

class ViewedQuoteFragment : QuoteBaseListFragment(R.layout.fragment_quote_list) {
    private val viewModel: ViewedQuoteViewModel by fragmentViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quoteBaseViewModel = viewModel

        viewModel.onAsync(ViewedQuoteState::theme, onSuccess = { theme ->
            binding.themeImageView.load(theme)
        })
        viewModel.onAsync(ViewedQuoteState::selectedQuote, onSuccess = { quote ->
            bindButtonBackground(quote.isFavorite)
        })
    }

    override fun epoxyController(): EpoxyController = simpleController(viewModel) { state ->
        val quotes = state.quotes.invoke()
        val fontRes = state.font.invoke() ?: FontRepository.DEFAULT_FONT
        val fontTypeface = resources.getFont(fontRes)
        quotes?.forEach { quoteModel ->
            quoteViewHolder {
                id(quoteModel.id)
                quote(quoteModel)
                font(fontTypeface)
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
