package com.example.quotes.epoxy.viewholders.settingsviewholders

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.example.quotes.R
import com.example.quotes.databinding.ItemFavoritesButtonBinding
import com.example.quotes.epoxy.ViewBindingEpoxyModelWithHolder

@EpoxyModelClass(layout = R.layout.item_favorites_button)
abstract class FavoritesButtonViewHolder : ViewBindingEpoxyModelWithHolder<ItemFavoritesButtonBinding>() {

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun ItemFavoritesButtonBinding.bind() {
        favoriteButton.setOnClickListener(onClickListener)
    }
}
