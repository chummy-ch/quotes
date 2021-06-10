package com.example.quotes.quote

import android.os.Bundle
import android.view.View
import coil.load
import com.airbnb.epoxy.VisibilityState
import com.airbnb.mvrx.fragmentViewModel
import com.example.quotes.R
import com.example.quotes.epoxy.simpleController
import com.example.quotes.epoxy.viewholders.quoteViewHolder
import com.example.quotes.settings.FontRepository
import com.google.android.material.snackbar.Snackbar

class QuoteListFragment : QuoteBaseListFragment(R.layout.fragment_quote_list) {

    private val viewModel: QuoteViewModel by fragmentViewModel()

    override fun epoxyController() = simpleController(viewModel) { state ->

        val fontRes = state.font.invoke() ?: FontRepository.DEFAULT_FONT
        val font = resources.getFont(fontRes)

        state.quotes.invoke()?.forEach { quoteModel ->
            quoteViewHolder {
                id(quoteModel.id)
                quote(quoteModel)
                font(font)
                onVisibilityStateChanged { model, view, visibilityState ->
                    if (visibilityState == VisibilityState.FOCUSED_VISIBLE) {
                        bindButtonBackground(quoteModel.isFavorite)
                        viewModel.addToViewed(quoteModel.id)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        quoteBaseViewModel = viewModel

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {
            onAsync(QuoteState::error, onFail = {
                Snackbar.make(view, it.toString(), 2000).show()
            })
            onAsync(QuoteState::theme, onSuccess = { theme ->
                binding.themeImageView.load(theme)
            })
            onAsync(QuoteState::selectedQuote, onSuccess = { quote ->
                bindButtonBackground(quote.isFavorite)
            })
        }
    }
}
