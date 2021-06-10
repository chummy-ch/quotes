package com.example.quotes.epoxy.viewholders.settingsviewholders

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.example.quotes.R
import com.example.quotes.databinding.ItemHistoryButtonBinding
import com.example.quotes.epoxy.ViewBindingEpoxyModelWithHolder

@EpoxyModelClass(layout = R.layout.item_history_button)
abstract class HistoryButtonViewHolder : ViewBindingEpoxyModelWithHolder<ItemHistoryButtonBinding>() {

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun ItemHistoryButtonBinding.bind() {
        historyButton.setOnClickListener(onClickListener)
    }
}
