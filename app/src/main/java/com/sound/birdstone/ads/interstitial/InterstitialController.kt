package com.sound.birdstone.ads.interstitial

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.sound.birdstone.BuildConfig
import com.sound.birdstone.R
import com.sound.birdstone.ads.openAd.AdsConstants.INTERSTITIAL_LOADING
import com.sound.birdstone.ads.openAd.AdsConstants.INTERSTITIAL_LOADING_TIME
import com.sound.birdstone.ads.openAd.AdsConstants.INTER_SHOWING
import com.sound.birdstone.ads.openAd.AdsConstants.inter_home_add_in_seconds
import com.sound.birdstone.ads.openAd.AdsConstants.inter_home_delay_in_seconds
import com.sound.birdstone.ads.openAd.AdsConstants.isAppInPause
import com.sound.birdstone.ads.openAd.AdsConstants.isOpenAdShowing
import com.sound.birdstone.ads.openAd.AdsConstants.time_base_inter_enable
import com.sound.birdstone.constants.Constants.Companion.showToast
import com.sound.birdstone.helper.InternetController
import com.sound.birdstone.helper.SaveValues
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.sound.birdstone.ads.AdLoadingDialog
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterstitialController @Inject constructor(
    private val internetConnection: InternetController,
    private val saveValue: SaveValues
) {
    private val handlerAd = Handler(Looper.getMainLooper())
    private var interCount = 0

    private var interArray = intArrayOf(
        R.string.interstitial_common,
        R.string.interstitial_common_1,
    )
    private var canShowAdWithTime = true
    private val runnableAd = Runnable {
        canShowAdWithTime = true
    }
    private var canRequestAd = true
    private var mInterstitialAd: InterstitialAd? = null
    private var adLoadingDialog: AdLoadingDialog? = null
    private var handlerSplash: Handler? = null
    private var runnableSplash: Runnable? = null
    private var mInterstitialControllerListener: InterstitialControllerListener? = null
    private var isPause = false
    private var isHandlerRunning = false
    fun initAdMob(
        context: Context,
        enable: Boolean
    ) {
        if (enable && internetConnection.isInternetConnected) {
            loadAd(context)
        }
    }


    private fun loadAd(
        context: Context
    ) {
        try {
            if (!saveValue.isAppPurchase() && mInterstitialAd == null && internetConnection.isInternetConnected) {
                if (!canRequestAd) {
                    return
                }
                canRequestAd = false
                if (interCount == interArray.size) {
                    interCount = 0
                }
                if (BuildConfig.DEBUG) {
                    showToast(context, "overAll inter calling")
                }
                InterstitialAd.load(context, context.getString(
                    interArray[interCount]
                ), AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        super.onAdLoaded(interstitialAd)
                        mInterstitialAd = interstitialAd
                        canRequestAd = true
                        if (BuildConfig.DEBUG) {
                            showToast(context, "overAll inter loaded")
                        }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        mInterstitialAd = null
                        canRequestAd = true
                        if (BuildConfig.DEBUG) {
                            showToast(
                                context,
                                "overAll inter failed to load ==> code " + loadAdError.code
                            )
                        }
                    }
                })
                interCount++
            }
        } catch (ignored: Exception) {
        }
    }

    private fun hideProgress(activity: Activity) {
        try {
            adLoadingDialog?.dismissAlertDialog(activity)
        } catch (ignored: Exception) {
        }
    }


    private fun showInterAd(
        activity: Activity, interstitialControllerListener: InterstitialControllerListener
    ) {
        try {
            if (mInterstitialAd != null && !isAppInPause) {
                mInterstitialAd?.show(activity)
            } else {
                interstitialControllerListener.onAdClosed()
            }
        } catch (e: Exception) {
            interstitialControllerListener.onAdClosed()
        }
    }


    fun showInterstitial(
        activity: Activity, enable: Boolean,
        interstitialControllerListener: InterstitialControllerListener,
        showWithDelay: Boolean = false
    ) {

        if (saveValue.isAppPurchase() || !enable || isOpenAdShowing) {
            interstitialControllerListener.onAdClosed()
        } else if (time_base_inter_enable && showWithDelay) {
            if (mInterstitialAd != null && canShowAdWithTime) {
                mInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            INTER_SHOWING = false
                            mInterstitialAd = null
                            interstitialControllerListener.onAdClosed()
                            if (time_base_inter_enable) {
                                canShowAdWithTime = false
                                inter_home_delay_in_seconds += inter_home_add_in_seconds
                                //delay 10 seconds between each navigation bottom click ad
                                handlerAd.postDelayed(
                                    runnableAd,
                                    inter_home_delay_in_seconds * 1000
                                )
                            }
                            loadAd(activity)
                        }

                        override fun onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent()
                            INTER_SHOWING = true
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            super.onAdFailedToShowFullScreenContent(adError)
                            INTER_SHOWING = false
                            mInterstitialAd = null
                            interstitialControllerListener.onAdClosed()
                        }
                    }
                if (INTERSTITIAL_LOADING) {
                    try {
                        adLoadingDialog = AdLoadingDialog(activity)
                        adLoadingDialog?.showAlertDialog(activity)
                        Handler(activity.mainLooper).postDelayed({
                            hideProgress(activity)
                            showInterAd(activity, interstitialControllerListener)
                        }, INTERSTITIAL_LOADING_TIME * 1000)
                    } catch (e: java.lang.Exception) {
                        hideProgress(activity)
                        showInterAd(activity, interstitialControllerListener)
                    }
                } else {
                    showInterAd(activity, interstitialControllerListener)
                }
            } else {
                interstitialControllerListener.onAdClosed()
            }
        } else if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    INTER_SHOWING = false
                    mInterstitialAd = null
                    interstitialControllerListener.onAdClosed()
                    loadAd(activity)
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    INTER_SHOWING = true
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    INTER_SHOWING = false
                    mInterstitialAd = null
                    interstitialControllerListener.onAdClosed()
                }
            }
            if (INTERSTITIAL_LOADING) {
                try {
                    adLoadingDialog = AdLoadingDialog(activity)
                    adLoadingDialog?.showAlertDialog(activity)
                    Handler(activity.mainLooper).postDelayed({
                        hideProgress(activity)
                        showInterAd(activity, interstitialControllerListener)
                    }, INTERSTITIAL_LOADING_TIME * 1000)
                } catch (e: Exception) {
                    hideProgress(activity)
                    showInterAd(activity, interstitialControllerListener)
                }
            } else {
                showInterAd(activity, interstitialControllerListener)
            }
        } else {
            interstitialControllerListener.onAdClosed()
            loadAd(activity)
        }
    }

    fun initSplashAdmob(
        activity: Activity,
        interstitialControllerListener: InterstitialControllerListener?
    ) {
        mInterstitialControllerListener = interstitialControllerListener
        isPause = false
        isHandlerRunning = false
        adLoadingDialog = AdLoadingDialog(activity)
        handlerSplash = Handler(Looper.getMainLooper())
        runnableSplash = Runnable {
            if (mInterstitialControllerListener != null && isHandlerRunning) {
                mInterstitialControllerListener?.onAdClosed()
            }
        }
        loadAdAndShow(activity, 10, true)
    }

    fun resumeAd(activity: Activity, delay: Long) {
        if (!saveValue.isAppPurchase()) {
            if (isPause) {
                if (!isHandlerRunning) {
                    if (mInterstitialAd != null) {
                        if (INTERSTITIAL_LOADING) {
                            runnableSplash = Runnable {
                                if (isHandlerRunning) {
                                    removeCallBacks()
                                    adLoadingDialog?.showAlertDialog(activity)
                                    startAdShowHandler(activity)
                                }
                            }
                            startHandler(1)
                        } else {
                            Handler(activity.mainLooper).postDelayed({
                                setFullScreenCallBack()
                                mInterstitialAd?.show(activity)
                            }, 1000)
                        }
                    } else {
                        startHandler(delay)
                    }
                }
            }
        }
    }

    private fun startAdShowHandler(activity: Activity) {
        runnableSplash = Runnable {
            if (isHandlerRunning) {
                adLoadingDialog?.dismissAlertDialog(activity)
                if (mInterstitialAd != null) {
                    setFullScreenCallBack()
                    mInterstitialAd?.show(activity)
                }
            }
        }
        startHandler(1)
    }

    private fun startHandler(time: Long) {
        if (handlerSplash != null && !isHandlerRunning) {
            isHandlerRunning = true
            handlerSplash?.postDelayed(runnableSplash!!, time * 1000)
        }
    }

    fun pauseAd() {
        isPause = true
        removeCallBacks()
    }

    fun destroyAd() {
        removeCallBacks()
    }

    private fun loadAdAndShow(
        activity: Activity, delay: Long, enable: Boolean
    ) {
        try {
            if (!saveValue.isAppPurchase() && enable && internetConnection.isInternetConnected) {
                if (mInterstitialAd != null) {
                    handlerSplash?.postDelayed({ showSplashAd(activity) }, 1000)
                    return
                }
                startHandler(delay)
                if (!canRequestAd) {
                    return
                }
                canRequestAd = false
                if (BuildConfig.DEBUG) {
                    showToast(activity, "splash inter calling")
                }
                InterstitialAd.load(
                    activity,
                    activity.getString(R.string.interstitial_splash_ad),
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            super.onAdLoaded(interstitialAd)
                            if (BuildConfig.DEBUG) {
                                showToast(activity, "splash inter loaded")
                            }
                            canRequestAd = true
                            mInterstitialAd = interstitialAd
                            showSplashAd(activity)
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            super.onAdFailedToLoad(loadAdError)
                            if (BuildConfig.DEBUG) {
                                showToast(
                                    activity,
                                    "splash inter failed to load ==> code " + loadAdError.code
                                )
                            }
                            canRequestAd = true
                            removeCallBacks()
                            callOnAdClose()
                        }
                    })
            } else {
                handlerSplash?.postDelayed({ callOnAdClose() }, 4000)
            }
        } catch (ignored: Exception) {
        }
    }

    private fun showSplashAd(activity: Activity) {
        removeCallBacks()
        if (!isPause && mInterstitialAd != null && !isOpenAdShowing) {
            if (INTERSTITIAL_LOADING) {
                adLoadingDialog?.showAlertDialog(activity)
                startAdShowHandler(activity)
            } else {
                setFullScreenCallBack()
                mInterstitialAd?.show(activity)
            }
        }
    }

    private fun setFullScreenCallBack() {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                INTER_SHOWING = false
                mInterstitialAd = null
                callOnAdClose()
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                INTER_SHOWING = true
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                super.onAdFailedToShowFullScreenContent(adError)
                INTER_SHOWING = false
                mInterstitialAd = null
                removeCallBacks()
                callOnAdClose()
            }
        }
    }

    private fun callOnAdClose() {
        mInterstitialControllerListener?.onAdClosed()
    }

    private fun removeCallBacks() {
        try {
            if (handlerSplash != null && runnableSplash != null) {
                isHandlerRunning = false
                handlerSplash?.removeCallbacks(runnableSplash!!)
            }
        } catch (ignored: Exception) {
        }
    }
}