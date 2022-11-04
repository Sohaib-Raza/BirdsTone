package com.sound.birdstone.ads.openAd

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.sound.birdstone.AppClass
import com.sound.birdstone.BuildConfig
import com.sound.birdstone.R
import com.sound.birdstone.ads.openAd.AdsConstants.canShowOpenAd
import com.sound.birdstone.ads.openAd.AdsConstants.isAppInPause
import com.sound.birdstone.ads.openAd.AdsConstants.isOpenAdShowing
import com.sound.birdstone.constants.Constants
import com.sound.birdstone.helper.InternetController
import com.sound.birdstone.helper.SaveValues
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.*

class AppOpenManager(


    private val appClass: AppClass,
    private val internetController: InternetController,
    private val saveValue: SaveValues

) : LifecycleObserver, DefaultLifecycleObserver {
    private var mAppOpenAd: AppOpenAd? = null
    private var loadTime: Long = 0
    private var canRequestAd = true
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        isAppInPause = false
        showAd()
    }

    private fun showAd() {
        try {
            if (canShowOpenAd && appClass.currentActivity != null && !saveValue.isAppPurchase()
                && AdsConstants.OPEN_AD_ENABLE
            ) {
                showAdIfAvailable()
            }
        } catch (ignored: Exception) {
        }
    }

    fun fetchAd() {
        // Have unused ad, no need to fetch another.
        if (isAdAvailable || !internetController.isInternetConnected || saveValue.isAppPurchase() || isAppInPause
        ) {
            return
        }
        if (!canRequestAd) {
            return
        }
        canRequestAd = false
        if (BuildConfig.DEBUG) {
            Constants.showToast(appClass, "Open Ad Called")
        }
        val loadCallback: AppOpenAdLoadCallback = object : AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                super.onAdLoaded(appOpenAd)
                canRequestAd = true
                if (BuildConfig.DEBUG) {
                    Constants.showToast(appClass, "Open Ad Loaded")
                }
                mAppOpenAd = appOpenAd
                mAppOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        mAppOpenAd = null
                        isShowingAd = false
                        isOpenAdShowing = false
                        fetchAd()
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                        isShowingAd = true
                        isOpenAdShowing = true
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        isOpenAdShowing = false
                    }


                }
                loadTime = Date().time
                //showAd();
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                canRequestAd = true
                mAppOpenAd = null
                if (BuildConfig.DEBUG) {
                    Constants.showToast(appClass, "Open Ad failed")
                }
            }
        }
        AppOpenAd.load(
            appClass, appClass.getString(R.string.add_app_open), AdRequest.Builder().build(),
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
        )
    }

    private fun showAdIfAvailable() {
        try {
            if (!isShowingAd && isAdAvailable) {
                if (!isAppInPause && !AdsConstants.INTER_SHOWING) {
                    mAppOpenAd?.show(appClass.currentActivity!!)
                }
            } else {
                fetchAd()
            }
        } catch (ignored: Exception) {
        }
    }

    private fun wasLoadTimeLessThanNHoursAgo(): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * 4L
    }

    private val isAdAvailable: Boolean
        get() = mAppOpenAd != null && wasLoadTimeLessThanNHoursAgo()


    companion object {
        private var isShowingAd = false
    }

    init {
        try {
            ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}