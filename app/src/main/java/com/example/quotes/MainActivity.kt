package com.example.quotes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.quotes.onboarding.OnboardingActivity
import com.example.quotes.quote.QuoteDetailsFragment
import com.example.quotes.quote.QuoteListFragment
import com.example.quotes.quote.QuoteModel
import kotlinx.coroutines.flow.first
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    companion object {
        const val channelId: String = "Quote"
        const val quoteIdExtra = "quoteModel"
        val ONBOARDING_KEY = booleanPreferencesKey("onboarding")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.hasExtra(quoteIdExtra)) {
            openQuoteDetailsDeepLink()
        } else if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<QuoteListFragment>(R.id.fragment_container)
            }
        }

        lifecycleScope.launchWhenCreated {
            val dataStore: DataStore<Preferences> by inject()
            val isOnboardingShowed = dataStore.data.first()[ONBOARDING_KEY] ?: false
            if (!isOnboardingShowed) showOnboarding()
        }
    }

    private fun showOnboarding() {
        startActivity(Intent(applicationContext, OnboardingActivity::class.java))
    }

    private fun openQuoteDetailsDeepLink() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            val quote = intent.getParcelableExtra<QuoteModel>(quoteIdExtra)
            if (quote == null) throw NullPointerException()
            else {
                val fragment: Fragment = QuoteDetailsFragment(quote)
                add(R.id.fragment_container, fragment)
            }
        }
    }
}
