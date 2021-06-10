package com.example.quotes.onboarding

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.airbnb.mvrx.fragmentViewModel
import com.example.quotes.R
import com.example.quotes.databinding.FragmentOnboardingBinding
import com.example.quotes.epoxy.MvRxListBaseFragment
import com.example.quotes.epoxy.simpleController
import com.example.quotes.epoxy.viewholders.onboardingviewholders.onboardingImageViewHolder
import com.example.quotes.epoxy.viewholders.onboardingviewholders.onboardingNotificationViewHolder
import com.example.quotes.epoxy.viewholders.settingsviewholders.NotificationPeriodChipViewHolder
import com.example.quotes.onboarding.viewmodels.OnboardingNotificationsViewModel

class OnboardingNotificationsFragment : MvRxListBaseFragment(R.layout.fragment_onboarding) {
    companion object {
        const val IMAGE_ID = "ring_image"
        const val NOTIFICATION_ID = "onboarding_notifications"
    }

    private val viewModel: OnboardingNotificationsViewModel by fragmentViewModel()
    private val binding: FragmentOnboardingBinding by viewBinding()
    private val changePeriodListener = object : NotificationPeriodChipViewHolder.PeriodListener {
        override fun changeNotificationPeriod(id: Int) {
            viewModel.saveNotificationPeriod(id)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.onboardingTitle.text = getString(R.string.onboarding_notification_title)
    }

    override fun epoxyController() = simpleController(viewModel) { state ->
        onboardingImageViewHolder {
            id(IMAGE_ID)
        }

        val notifications = viewModel.getNotificationsList()
        val selectedNotification = state.notification.invoke() ?: 0
        onboardingNotificationViewHolder {
            id(NOTIFICATION_ID)
            notifications(notifications)
            selectedNotificationId(selectedNotification)
            periodListener(changePeriodListener)
        }
    }
}