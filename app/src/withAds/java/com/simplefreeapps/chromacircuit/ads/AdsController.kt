package com.simplefreeapps.chromacircuit.ads

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.simplefreeapps.chromacircuit.R

/**
 * Flavor-isolated AdMob controller for `withAds` builds.
 * Pre-configured strictly with official Google TEST IDs in `withAds/res/values/admob.xml`.
 */
object AdsController {
    private var initialized = false
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var lastInterstitialMs = 0L

    fun init(activity: Activity) {
        if (initialized) return
        initialized = true
        MobileAds.initialize(activity.applicationContext) {
            preloadInterstitial(activity)
            preloadRewarded(activity)
        }
    }

    private fun preloadInterstitial(context: Context) {
        val unitId = context.getString(R.string.admob_interstitial_id)
        InterstitialAd.load(
            context,
            unitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            },
        )
    }

    private fun preloadRewarded(context: Context) {
        val unitId = context.getString(R.string.admob_rewarded_id)
        RewardedAd.load(
            context,
            unitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                }
            },
        )
    }

    fun showInterstitialIfReady(activity: Activity, onDone: () -> Unit) {
        val now = System.currentTimeMillis()
        val ad = interstitialAd
        if (ad == null || now - lastInterstitialMs < 45_000L) {
            onDone()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                lastInterstitialMs = System.currentTimeMillis()
                preloadInterstitial(activity)
                onDone()
            }

            override fun onAdFailedToShowFullScreenContent(err: AdError) {
                interstitialAd = null
                preloadInterstitial(activity)
                onDone()
            }
        }
        ad.show(activity)
    }

    fun showRewardedHint(activity: Activity, onGranted: () -> Unit) {
        val ad = rewardedAd
        if (ad == null) {
            preloadRewarded(activity)
            onGranted()
            return
        }
        var earned = false
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                preloadRewarded(activity)
                if (earned) onGranted()
            }

            override fun onAdFailedToShowFullScreenContent(err: AdError) {
                rewardedAd = null
                preloadRewarded(activity)
                onGranted()
            }
        }
        ad.show(activity) {
            earned = true
        }
    }

    @Composable
    fun BannerSlot(context: Context, modifier: Modifier = Modifier) {
        val unitId = context.getString(R.string.admob_banner_id)
        AndroidView(
            modifier = modifier.fillMaxWidth(),
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = unitId
                    loadAd(AdRequest.Builder().build())
                }
            },
        )
    }
}
