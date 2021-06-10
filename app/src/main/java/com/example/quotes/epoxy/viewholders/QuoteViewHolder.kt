package com.example.quotes.epoxy.viewholders

import android.graphics.Typeface
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.example.quotes.R
import com.example.quotes.databinding.ItemQuoteBinding
import com.example.quotes.epoxy.ViewBindingEpoxyModelWithHolder
import com.example.quotes.quote.QuoteModel

@EpoxyModelClass(layout = R.layout.item_quote)
abstract class QuoteViewHolder : ViewBindingEpoxyModelWithHolder<ItemQuoteBinding>() {

    @EpoxyAttribute
    lateinit var quote: QuoteModel

    @EpoxyAttribute
    lateinit var font: Typeface

    override fun ItemQuoteBinding.bind() {
        quoteText.text = quote.text
        quoteText.typeface = font
    }
}
