package com.example.quotes.epoxy.viewholders.onboardingviewholders

import com.airbnb.epoxy.EpoxyModelClass
import com.example.quotes.R
import com.example.quotes.databinding.ItemRoundImageBinding
import com.example.quotes.epoxy.ViewBindingEpoxyModelWithHolder

@EpoxyModelClass(layout = R.layout.item_round_image)
abstract class OnboardingImageViewHolder : ViewBindingEpoxyModelWithHolder<ItemRoundImageBinding>() {

    override fun ItemRoundImageBinding.bind() {
        ivRing.setImageResource(R.drawable.ic_ring)
        ivRingBackground.setImageResource(R.drawable.onboarding_notification_background)
    }
}
