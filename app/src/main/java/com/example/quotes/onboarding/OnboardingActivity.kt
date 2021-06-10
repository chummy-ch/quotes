package com.example.quotes.onboarding

import android.os.Bundle
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.adapty.Adapty
import com.example.quotes.MainActivity
import com.example.quotes.R
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroCustomLayoutFragment
import org.koin.android.ext.android.inject

class OnboardingActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        addSlide(
            AppIntroCustomLayoutFragment.newInstance(R.layout.fragment_onboarding_quote)
        )
        addSlide(
            OnboardingCategoriesFragment()
        )
        addSlide(
            OnboardingNotificationsFragment()
        )
        addSlide(
            OnboardingPremiumFragment()
        )

    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        saveOnboardingState()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        makePurchase()
    }

    private fun makePurchase() {
        val repository: PremiumRepository by inject()
        val product = repository.getSelectedProduct() ?: return
        Adapty.makePurchase(this, product) { purchaserInfo, purchaseToken, googleValidationResult, p, error ->
            if (error == null) {
                Toast.makeText(this, getString(R.string.premium_buy_success), Toast.LENGTH_SHORT).show()
                saveOnboardingState()
            } else Toast.makeText(this, getString(R.string.premium_buy_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveOnboardingState() {
        lifecycleScope.launchWhenCreated {
            val dataStore: DataStore<Preferences> by inject()
            dataStore.edit { mutablePreferences ->
                mutablePreferences[MainActivity.ONBOARDING_KEY] = true
            }
            finish()
        }
    }
}