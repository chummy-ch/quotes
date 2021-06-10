package com.example.quotes.epoxy.viewholders.settingsviewholders

import android.graphics.Color
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.example.quotes.R
import com.example.quotes.databinding.ItemEmptyLinearLayoutBinding
import com.example.quotes.epoxy.ViewBindingEpoxyModelWithHolder

@EpoxyModelClass(layout = R.layout.item_empty_linear_layout)
abstract class HeaderViewHolder : ViewBindingEpoxyModelWithHolder<ItemEmptyLinearLayoutBinding>() {

    @EpoxyAttribute
    lateinit var header: String

    override fun ItemEmptyLinearLayoutBinding.bind() {
        root.removeAllViews()
        val textView = TextView(root.context).apply {
            textSize = 20f
            text = header
            setTextColor(Color.BLACK)
        }
        root.addView(textView)
    }
}
