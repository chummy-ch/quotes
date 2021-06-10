package com.example.quotes

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.adapty.Adapty
import com.adapty.utils.AdaptyLogLevel
import com.airbnb.mvrx.Mavericks
import com.example.quotes.di.repositoryModule
import com.example.quotes.quote.QuoteRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.context.startKoin

class QuoteApp : Application() {
    companion object {
        private const val adaptyPublicKey = "public_live_weHAh5uq.uX0jGZofqJ8YXD0iU95F"
    }

    lateinit var quoteRepository: QuoteRepository
    lateinit var koin: Koin

    override fun onCreate() {
        super.onCreate()

        koin = startKoin {
            modules(listOf(repositoryModule))
            androidContext(applicationContext)
        }.koin
        quoteRepository = koin.get(QuoteRepository::class)
        Adapty.Companion.activate(applicationContext, adaptyPublicKey)
        Adapty.setLogLevel(AdaptyLogLevel.VERBOSE)

        Mavericks.initialize(this)

        //Track activity for ad Repository
        registerActivityLifecycleCallbacks(getActivityLifecycleCallbacks())
    }

    private fun getActivityLifecycleCallbacks() = object : ActivityLifecycleCallbacks {
        override fun onActivityCreated(p0: Activity, p1: Bundle?) {
            //TODO("Not yet implemented")
        }

        override fun onActivityStarted(p0: Activity) {
            //TODO("Not yet implemented")
        }

        override fun onActivityResumed(p0: Activity) {
            koin.get(AdRepository::class).activity = p0
        }

        override fun onActivityPaused(p0: Activity) {
            koin.get(AdRepository::class).activity = null
        }

        override fun onActivityStopped(p0: Activity) {
            //TODO("Not yet implemented")
        }

        override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
            //TODO("Not yet implemented")
        }

        override fun onActivityDestroyed(p0: Activity) {
            //TODO("Not yet implemented")
        }
    }
}
