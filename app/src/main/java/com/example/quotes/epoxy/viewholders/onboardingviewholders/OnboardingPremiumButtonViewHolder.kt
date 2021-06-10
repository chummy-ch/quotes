package com.example.quotes.epoxy.viewholders.onboardingviewholders

import android.graphics.Color
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.example.quotes.R
import com.example.quotes.databinding.ItemOnboardingPremiumButtonsBinding
import com.example.quotes.epoxy.ViewBindingEpoxyModelWithHolder

@EpoxyModelClass(layout = R.layout.item_onboarding_premium_buttons)
abstract class OnboardingPremiumButtonViewHolder :
    ViewBindingEpoxyModelWithHolder<ItemOnboardingPremiumButtonsBinding>() {

    @EpoxyAttribute
    lateinit var monthPrice: String

    @EpoxyAttribute
    lateinit var yearPrice: String

    @EpoxyAttribute
    lateinit var pricePerWeek: String

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var premiumSelect: PremiumSelect

    override fun ItemOnboardingPremiumButtonsBinding.bind() {
        tvYearPrice.text = yearPrice
        tvMonthPrice.text = monthPrice
        tvYearPricePerWeek.text = root.context.getString(R.string.premium_price_per_week, pricePerWeek)
        val context = root.context
        buttonMonth.setOnClickListener {
            premiumSelect.selectMonthMonthPremium()
            ivTickMonth.isVisible = true
            ivTickAnnual.isVisible = false
            tvTrial.setTextColor(Color.BLACK)
            tvYearPrice.setTextColor(Color.BLACK)
            tvYearPeriod.setTextColor(Color.BLACK)
            tvYearPricePerWeek.setTextColor(Color.BLACK)
            buttonMonth.background = AppCompatResources.getDrawable(context, R.drawable.button_black)
            buttonYear.background = AppCompatResources.getDrawable(context, R.drawable.button_white)
            tvMonthPreiod.setTextColor(Color.WHITE)
            tvMonthPrice.setTextColor(Color.WHITE)

        }
        buttonYear.setOnClickListener {
            premiumSelect.selectAnnualPremium()
            ivTickMonth.isVisible = false
            ivTickAnnual.isVisible = true
            tvYearPeriod.setTextColor(Color.WHITE)
            tvTrial.setTextColor(root.context.getColor(R.color.grey))
            tvYearPrice.setTextColor(Color.WHITE)
            tvYearPricePerWeek.setTextColor(root.context.getColor(R.color.grey))
            buttonYear.background = AppCompatResources.getDrawable(context, R.drawable.button_black)
            buttonMonth.background = AppCompatResources.getDrawable(context, R.drawable.button_white)
            tvMonthPreiod.setTextColor(Color.BLACK)
            tvMonthPrice.setTextColor(Color.BLACK)
        }
    }

    interface PremiumSelect {
        fun selectAnnualPremium()

        fun selectMonthMonthPremium()
    }
}