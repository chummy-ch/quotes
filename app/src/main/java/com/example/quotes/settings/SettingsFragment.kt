package com.example.quotes.settings

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import by.kirich1409.viewbindingdelegate.viewBinding
import com.airbnb.mvrx.fragmentViewModel
import com.example.quotes.BuildConfig
import com.example.quotes.FBAnalytics
import com.example.quotes.R
import com.example.quotes.databinding.FragmentSettingsBinding
import com.example.quotes.dialogwindows.AdDialogFragment
import com.example.quotes.epoxy.MvRxListBaseFragment
import com.example.quotes.epoxy.simpleController
import com.example.quotes.epoxy.viewholders.settingsviewholders.*
import com.example.quotes.notification.NotificationTimeRepository
import com.example.quotes.onboarding.OnboardingPremiumFragment
import com.example.quotes.quote.favorite.FavoriteQuoteFragment
import com.example.quotes.quote.viewed.ViewedQuoteFragment
import com.example.quotes.theme.ThemesListFragment

class SettingsFragment : MvRxListBaseFragment(R.layout.fragment_settings) {
    companion object {
        const val FAVORITES_BUTTON_ID = "favorite_button"
        const val BACKGROUND_BUTTON_ID = "background_button"
        const val CATEGORY_HEADER_ID = "category_header"
        const val FONT_HEADER_ID = "font_header"
        const val NOTIFICATION_HEADER_ID = "notification_header"
        const val CATEGORY_CHIPS_ID = "category_chips"
        const val FONT_CHIPS_ID = "font_chips"
        const val NOTIFICATION_CHIP_ID = "notification_chips"
        const val NOTIFICATION_TIME_ID = "notification_time"
        const val HISTORY_BUTTON_ID = "history_button"
        const val FRAGMENT_TAG = "settings_fragment"
    }

    private val binding: FragmentSettingsBinding by viewBinding()
    private val viewModel: SettingsViewModel by fragmentViewModel()

    private val setCategoryListener = object : CategoryChipViewHolder.CategoryListener {
        override fun selectCategory(id: Long) {
            viewModel.selectCategory(id)
            FBAnalytics.getSetContentAnalytics(id, getString(R.string.category_selected))
        }

        override fun unselectCategory(id: Long) {
            viewModel.unselectCategory(id)
            FBAnalytics.getSetContentAnalytics(id, getString(R.string.category_unselected))
        }

        override fun unlock(id: Long) {
            val getPremium: () -> Unit = {
                // TODO: 17.04.21 Not implemented yet
            }
            val showAd: () -> Unit = {
                viewModel.showAd(id)
            }
            val dialog = AdDialogFragment(showAd, getPremium)
            dialog.show(parentFragmentManager, null)
        }
    }
    private val setFontListener = object : FontChipViewHolder.FontListener {

        override fun changeFont(res: Int) {
            viewModel.saveFont(res)
        }
    }
    private val changePeriodListener = object : NotificationPeriodChipViewHolder.PeriodListener {
        override fun changeNotificationPeriod(id: Int) {
            viewModel.saveNotificationPeriod(id)
        }

    }
    private val changeAlarmListener = View.OnClickListener {
        val timePickerListener = TimePickerDialog.OnTimeSetListener { timePicker, hours, minutes ->
            val time = hours * 60 + minutes
            viewModel.saveAlarm(time.toLong())
        }
        TimePickerDialog(requireContext(), timePickerListener, 9, 0, true).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (BuildConfig.DEBUG) {
            binding.buttonPremiumFragment.isVisible = true
            binding.buttonPremiumFragment.setOnClickListener { startFragment(OnboardingPremiumFragment()) }
        } else binding.buttonPremiumFragment.isVisible = false
    }

    override fun epoxyController() = simpleController(viewModel) { state ->

        if (state.hasFavorites.invoke() == true) {
            favoritesButtonViewHolder {
                id(FAVORITES_BUTTON_ID)
                onClickListener(View.OnClickListener { startFragment(FavoriteQuoteFragment()) })
            }
        }

        themesButtonViewHolder {
            id(BACKGROUND_BUTTON_ID)
            onClickListener(View.OnClickListener { startFragment(ThemesListFragment()) })
        }

        historyButtonViewHolder {
            id(HISTORY_BUTTON_ID)
            onClickListener(View.OnClickListener { startFragment(ViewedQuoteFragment()) })
        }

        headerViewHolder {
            id(CATEGORY_HEADER_ID)
            header(getString(R.string.categories_header))
        }

        val categories = state.categories.invoke()
        if (categories != null) {
            categoryChipViewHolder {
                id(CATEGORY_CHIPS_ID)
                categories(categories)
                categoryListener(setCategoryListener)
            }
        }

        headerViewHolder {
            id(FONT_HEADER_ID)
            header(getString(R.string.fonts_header))
        }

        val fonts = state.fonts.invoke()
        if (fonts != null) {
            fontChipViewHolder {
                id(FONT_CHIPS_ID)
                fonts(fonts)
                fontOnClick(setFontListener)
            }
        }

        headerViewHolder {
            id(NOTIFICATION_HEADER_ID)
            header(getString(R.string.notify_header))
        }

        val notificationList = viewModel.getNotificationList()
        val id = state.notification.invoke() ?: 0
        notificationPeriodChipViewHolder {
            id(NOTIFICATION_CHIP_ID)
            periodListener(changePeriodListener)
            notifications(notificationList)
            selectedNotificationId(id)
        }

        val alarms = state.alarms.invoke() ?: listOf(NotificationTimeRepository.DEFAULT_ALARM)
        timeNotificationChipsViewHolder {
            id(NOTIFICATION_TIME_ID)
            timeList(alarms)
            changeAlarm(changeAlarmListener)
        }
    }

    private fun startFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.commit {
            setReorderingAllowed(true)
            addToBackStack(null)
            replace(R.id.fragment_container, fragment)
        }
    }
}
