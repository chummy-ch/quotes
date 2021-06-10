package com.example.quotes.epoxy.viewholders.settingsviewholders

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.example.quotes.R
import com.example.quotes.databinding.ItemThemesButtonBinding
import com.example.quotes.epoxy.ViewBindingEpoxyModelWithHolder

@EpoxyModelClass(layout = R.layout.item_themes_button)
abstract class ThemesButtonViewHolder : ViewBindingEpoxyModelWithHolder<ItemThemesButtonBinding>() {

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun ItemThemesButtonBinding.bind() {
        backgroundButton.setOnClickListener(onClickListener)
    }
}
