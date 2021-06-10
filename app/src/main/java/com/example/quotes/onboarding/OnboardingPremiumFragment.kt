package com.example.quotes.onboarding

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.airbnb.mvrx.fragmentViewModel
import com.example.quotes.MainActivity
import com.example.quotes.R
import com.example.quotes.databinding.FragmentPremiumBinding
import com.example.quotes.epoxy.MvRxListBaseFragment
import com.example.quotes.epoxy.simpleController
import com.example.quotes.epoxy.viewholders.onboardingviewholders.OnboardingPremiumButtonViewHolder
import com.example.quotes.epoxy.viewholders.onboardingviewholders.onboardingPremiumButtonViewHolder
import com.example.quotes.onboarding.viewmodels.PremiumViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import org.koin.android.ext.android.inject
import java.math.BigDecimal
import java.math.RoundingMode

class OnboardingPremiumFragment : MvRxListBaseFragment(R.layout.fragment_premium) {
    companion object {
        const val PREMIUM_BUTTONS_ID = "premium_buttons"
        const val ANNUAL_SUB_ID = "quote_annual"
        const val MONTHLY_SUB_ID = "monthly_sub"
    }

    private val viewModel: PremiumViewModel by fragmentViewModel()
    private val binding: FragmentPremiumBinding by viewBinding()

    private val premiumSelect = object : OnboardingPremiumButtonViewHolder.PremiumSelect {
        override fun selectAnnualPremium() {
            viewModel.selectPremiumProduct(ANNUAL_SUB_ID)
        }

        override fun selectMonthMonthPremium() {
            viewModel.selectPremiumProduct(MONTHLY_SUB_ID)
        }
    }

    override fun epoxyController() = simpleController(viewModel) { state ->
        val products = state.products.invoke()
        var annualPrice = ""
        var monthlyPrice = ""
        var pricePerWeek = ""
        products?.forEach { product ->
            val priceString = product.localizedPrice
            val price = product.price
            if (product.vendorProductId == ANNUAL_SUB_ID && priceString != null) {
                annualPrice = priceString
                if (price != null) pricePerWeek = price.divide(BigDecimal(12), RoundingMode.HALF_DOWN).toString()
            } else if (product.vendorProductId == MONTHLY_SUB_ID && priceString != null) {
                monthlyPrice = priceString
            }
        }
        onboardingPremiumButtonViewHolder {
            id(PREMIUM_BUTTONS_ID)
            monthPrice(monthlyPrice)
            yearPrice(annualPrice)
            pricePerWeek(pricePerWeek)
            premiumSelect(premiumSelect)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mediaItem =
            MediaItem.fromUri(Uri.parse("android.resource://com.smartfoxlabs.daily.motivation.quotes.pictures.status.meditation/raw/beach_background"))
        val player = SimpleExoPlayer.Builder(requireContext()).build()
        binding.styledPlayer.player = player
        player.apply {
            setMediaItem(mediaItem)
            setMenuVisibility(false)
            skipSilenceEnabled = false
            repeatMode = Player.REPEAT_MODE_ALL
            prepare()
            play()
        }
        binding.textViewTitle.text = getString(R.string.onboarding_premium_title)
        binding.tvFreeVersion.setOnClickListener {
            val dataStore: DataStore<Preferences> by inject()
            lifecycleScope.launchWhenCreated {
                dataStore.edit {
                    it[MainActivity.ONBOARDING_KEY] = true
                }
                activity?.finish()
            }
        }
    }
}
