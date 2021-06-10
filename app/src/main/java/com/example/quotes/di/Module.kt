package com.example.quotes.di

import androidx.datastore.preferences.createDataStore
import androidx.work.WorkManager
import com.example.quotes.AdRepository
import com.example.quotes.ConfigRepository
import com.example.quotes.category.CategoriesRepository
import com.example.quotes.notification.NotificationRepository
import com.example.quotes.notification.NotificationTimeRepository
import com.example.quotes.notification.NotificationUsecase
import com.example.quotes.onboarding.PremiumRepository
import com.example.quotes.quote.QuoteRepository
import com.example.quotes.settings.FontRepository
import com.example.quotes.theme.ThemeRepository
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val repositoryModule = module {
    val db = Firebase.firestore.apply {
        firestoreSettings {
            isPersistenceEnabled = true
            cacheSizeBytes = FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
        }
    }
    val defaults = mapOf<String, Long>(
        ConfigRepository.AD_KEY to 2,
        //Default remote version should be 1, coz if we dont have cache, the local version will be 0.
        //So we need to force loading from the remote server
        QuoteRepository.REMOTE_STORE_VERSION_KEY to 1
    )
    val remoteConfig = FirebaseRemoteConfig.getInstance().apply {
        setConfigSettingsAsync(remoteConfigSettings { minimumFetchIntervalInSeconds = 3600 })
        setDefaultsAsync(defaults)
        fetchAndActivate()
    }
    single {
        androidContext().createDataStore(
            name = "settings"
        )
    }
    single { QuoteRepository(db, remoteConfig, get()) }
    single { ThemeRepository(get()) }
    single { CategoriesRepository(db, remoteConfig, get()) }
    single { AdRepository() }
    single { ConfigRepository(remoteConfig, get()) }
    single { FontRepository(get()) }
    single { NotificationRepository(get()) }
    single { NotificationTimeRepository(get()) }
    single { NotificationUsecase(WorkManager.getInstance(androidContext()), androidContext()) }
    single { PremiumRepository() }
}