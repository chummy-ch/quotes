package com.example.quotes.epoxy.viewholders.onboardingviewholders

import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.example.quotes.R
import com.example.quotes.category.CategoryPresentationModel
import com.example.quotes.category.Unlocked
import com.example.quotes.databinding.ItemQuoteCategoryBinding
import com.example.quotes.epoxy.ViewBindingEpoxyModelWithHolder

@EpoxyModelClass(layout = R.layout.item_quote_category)
abstract class CategoryViewHolder : ViewBindingEpoxyModelWithHolder<ItemQuoteCategoryBinding>() {

    @EpoxyAttribute
    lateinit var category: CategoryPresentationModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var categoryListener: CategoryListener

    override fun ItemQuoteCategoryBinding.bind() {
        categoryNameTextView.text = category.name
        categoryImage.setBackgroundColor(category.icon)

        val status = category.status
        if (status is Unlocked) categoryTick.isVisible = status.isSelected
        else categoryTick.isVisible = false

        parentCardView.setOnClickListener {
            if (status is Unlocked) {
                when (status.isSelected) {
                    true -> categoryListener.unselectCategory(category.id)
                    false -> categoryListener.selectCategory(category.id)
                }
            }
        }
    }

    interface CategoryListener {

        fun selectCategory(id: Long)

        fun unselectCategory(id: Long)
    }
}
