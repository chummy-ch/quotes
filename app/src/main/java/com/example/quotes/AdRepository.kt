package com.example.quotes

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.*
import kotlin.coroutines.resume

class AdRepository() {
    companion object {
        private const val TAG = "Advertisement"
        private const val adKey = "ca-app-pub-3940256099942544/5224354917"
        private const val productionAdKey = "ca-app-pub-1124900564123572/6358150287"
    }

    enum class AdResult {
        SHOWED, FAILED, CLAIMED
    }

    var activity: Activity? = null
        set(value) {
            field = value
            if (value != null) loadAd()
        }
    private var rewardedAd: RewardedAd? = null
    private var interstitialAd: InterstitialAd? = null
    private var result = AdResult.FAILED

    private val rewardedAdLoadCallback = object : RewardedAdLoadCallback() {
        override fun onAdFailedToLoad(p0: LoadAdError) {
            rewardedAd = null
        }

        override fun onAdLoaded(p0: RewardedAd) {
            Log.d(TAG, "Ad is loaded")
            rewardedAd = p0
        }
    }

    private fun loadAd() {
        if (activity == null || rewardedAd != null) return
        val adRequest = AdRequest.Builder().build()
        val advertisementKey = when (BuildConfig.DEBUG) {
            true -> adKey
            false -> productionAdKey
        }
        RewardedAd.load(activity as Context, advertisementKey, adRequest, rewardedAdLoadCallback)
    }

    suspend fun bindAdAndGetResult() = suspendCancellableCoroutine<AdResult> { cont ->
        val rewardAd = rewardedAd
        val activity = activity
        result = AdResult.FAILED
        if (activity == null) {
            cont.resume(result)
            return@suspendCancellableCoroutine
        }
        if (rewardAd == null) {
            loadAd()
            cont.resume(result)
            return@suspendCancellableCoroutine
        }
        rewardAd.fullScreenContentCallback = createFullScreenContentCallback(cont)

        rewardAd.show(activity, getonUserEarnedRewardListener(cont))
    }

    private fun getonUserEarnedRewardListener(cont: CancellableContinuation<AdResult>) =
        OnUserEarnedRewardListener {
            //If we get the reward then the user has watched the ad and we can resume
            result = AdResult.CLAIMED
        }

    private fun createFullScreenContentCallback(cont: CancellableContinuation<AdResult>) =
        object : FullScreenContentCallback() {

            //If Fail to load we returns failed else we are waiting for the rewardListener
            override fun onAdFailedToShowFullScreenContent(p0: AdError?) {
                Log.d(TAG, "Fail")
                result = AdResult.FAILED
                rewardedAd = null
                cont.resume(result)
            }

            //in the showed fun we just makes ad null
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Showed")
                result = AdResult.SHOWED
                rewardedAd = null
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                cont.resume(result)
            }
        }
}
