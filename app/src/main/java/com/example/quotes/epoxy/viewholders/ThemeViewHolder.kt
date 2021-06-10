package com.example.quotes.epoxy.viewholders

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.example.quotes.R
import com.example.quotes.databinding.ItemThemeBinding
import com.example.quotes.epoxy.ViewBindingEpoxyModelWithHolder
import com.google.firebase.storage.StorageReference
import io.github.rosariopfernandes.firecoil.load


@EpoxyModelClass(layout = R.layout.item_theme)
abstract class ThemeViewHolder : ViewBindingEpoxyModelWithHolder<ItemThemeBinding>() {

    @EpoxyAttribute
    lateinit var theme: StorageReference

    @EpoxyAttribute
    var quote: String = ""

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var themeListener: ThemeListener

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var longPress: ThemeLongPress


    override fun ItemThemeBinding.bind() {
        themeQuote.text = quote
        themeImageView.load(theme)
        materialCardParent.setOnClickListener { themeListener.applyTheme(theme) }
        materialCardParent.setOnLongClickListener {
            longPress.createDialogWindow(
                theme,
                quote,
                it,
                theme.toString()
            )
        }
    }

    interface ThemeListener {
        fun applyTheme(theme: StorageReference)
    }

    interface ThemeLongPress {
        fun createDialogWindow(theme: StorageReference, text: String, image: View, imageName: String): Boolean
    }
}
