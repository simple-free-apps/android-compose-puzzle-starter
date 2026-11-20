package com.simplefreeapps.chromacircuit.ads

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Flavor-isolated AdBridge for `noAds` builds: zero AdMob classes compiled.
 */
object AdsController {
    fun init(activity: Activity) = Unit

    fun showInterstitialIfReady(activity: Activity, onDone: () -> Unit) {
        onDone()
    }

    fun showRewardedHint(activity: Activity, onGranted: () -> Unit) {
        onGranted()
    }

    @Composable
    fun BannerSlot(context: Context, modifier: Modifier = Modifier) = Unit
}
