package com.example.quotes.epoxy.viewholders.settingsviewholders

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.example.quotes.R
import com.example.quotes.category.CategoryPresentationModel
import com.example.quotes.category.Locked
import com.example.quotes.category.Premium
import com.example.quotes.category.Unlocked
import com.example.quotes.databinding.ItemChipGroupBinding
import com.example.quotes.epoxy.ViewBindingEpoxyModelWithHolder
import com.example.quotes.views.MyChip

@EpoxyModelClass(layout = R.layout.item_chip_group)
abstract class CategoryChipViewHolder : ViewBindingEpoxyModelWithHolder<ItemChipGroupBinding>() {

    @EpoxyAttribute
    lateinit var categories: List<CategoryPresentationModel>

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var categoryListener: CategoryListener

    override fun ItemChipGroupBinding.bind() {
        root.removeAllViews()
        categories.forEach { category ->

            val chip = MyChip(root.context).apply {
                text = category.name
                //How to get size from dimens. 18sp from res are too big
                textSize = 18f

                bindStatus(category, this)
                setOnClickListener(getCategoryOnClick(category))

            }
            categoryChipGroup.addView(chip)
        }
    }

    private fun bindStatus(category: CategoryPresentationModel, chip: MyChip) {
        with(chip) {
            when (category.status) {
                is Locked -> {
                    isCloseIconVisible = true
                    closeIcon = AppCompatResources.getDrawable(context, R.drawable.ic_statuc_lock)
                    bindSelectionStatus(false)
                }
                is Premium -> {
                    chipIcon = AppCompatResources.getDrawable(context, R.drawable.ic_status_premium)
                    bindSelectionStatus(false)
                }
                is Unlocked -> bindSelectionStatus(category.status.isSelected)
            }
        }
    }

    private fun getCategoryOnClick(category: CategoryPresentationModel) = View.OnClickListener {
        if (category.status is Unlocked) {
            when (category.status.isSelected) {
                true -> categoryListener.unselectCategory(category.id)

                false -> categoryListener.selectCategory(category.id)
            }
        } else if (category.status is Locked) categoryListener.unlock(category.id)

    }

    interface CategoryListener {

        fun selectCategory(id: Long)

        fun unselectCategory(id: Long)

        fun unlock(id: Long)
    }
}
