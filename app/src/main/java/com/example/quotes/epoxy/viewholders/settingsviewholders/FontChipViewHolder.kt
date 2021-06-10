package com.example.quotes.epoxy.viewholders.settingsviewholders

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.example.quotes.views.MyChip
import com.example.quotes.R
import com.example.quotes.databinding.ItemChipGroupBinding
import com.example.quotes.epoxy.ViewBindingEpoxyModelWithHolder
import com.example.quotes.settings.FontModel

@EpoxyModelClass(layout = R.layout.item_chip_group)
abstract class FontChipViewHolder : ViewBindingEpoxyModelWithHolder<ItemChipGroupBinding>() {

    @EpoxyAttribute
    lateinit var fonts: List<FontModel>

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var fontOnClick: FontListener

    override fun ItemChipGroupBinding.bind() {
        root.removeAllViews()
        fonts.forEach { font ->
            val chip = MyChip(root.context).apply {
                text = font.name
                textSize = 18f
                val textFont = context.resources.getFont(font.res)
                typeface = textFont
                setOnClickListener { fontOnClick.changeFont(font.res) }
                bindSelectionStatus(font.isSelected)
            }
            categoryChipGroup.addView(chip)
        }
    }

    interface FontListener {
        fun changeFont(res: Int)
    }
}
