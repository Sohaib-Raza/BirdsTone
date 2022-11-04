package com.sound.birdstone.ads.bannerAds

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.sound.birdstone.BuildConfig
import com.sound.birdstone.ads.openAd.AdsConstants
import com.sound.birdstone.constants.Constants.Companion.showToast
import com.sound.birdstone.helper.InternetController
import com.sound.birdstone.helper.SaveValues
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.sound.birdstone.ads.AdControllerListener
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BannerAdController @Inject constructor(
    private val internetController: InternetController,
    private val saveValue: SaveValues
) {
    private var canRequestBannerAd = true
    private var adView: AdView? = null
    private var adControllerListener: AdControllerListener? = null

    private var admobBannerIdCount = 0

    private var admobBannerIdArray = intArrayOf(

    )

    fun setAdControllerListener(listener: AdControllerListener?) {
        adControllerListener?.resetRequest()
        adControllerListener = listener
    }

    fun loadBannerAd(
        context: Activity, enable: Boolean
    ) {
        try {
            if (enable && !saveValue.isAppPurchase() && adView == null &&
                internetController.isInternetConnected
            ) {
                if (!canRequestBannerAd) {
                    return
                }
                canRequestBannerAd = false
                if (BuildConfig.DEBUG) {
                    showToast(context, "banner ad calling")
                }
                if (admobBannerIdCount == admobBannerIdArray.size) {
                    admobBannerIdCount = 0
                }
                val bannerAd = AdView(context).apply {
                    this.adUnitId = context.getString(admobBannerIdArray[admobBannerIdCount])
                    this.setAdSize(AdsConstants.getAdSize(context))
                    this.loadAd(AdRequest.Builder().build())
                }
                bannerAd.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        canRequestBannerAd = true
                        if (BuildConfig.DEBUG) {
                            showToast(context, "banner ad loaded")
                        }
                        bannerAd.adListener = object : AdListener() {
                        }
                        adView = bannerAd
                        adControllerListener?.onAdLoaded()
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        canRequestBannerAd = true
                        adView = null
                        if (BuildConfig.DEBUG) {
                            showToast(
                                context,
                                "banner load failed ==> code " + p0.code
                            )
                        }
                        adControllerListener?.onAdFailed()
                    }
                }
                admobBannerIdCount++
            }
        } catch (ignored: Exception) {
        }
    }

    fun populateBannerAd(
        context: Activity, enable: Boolean,
        adFrame: LinearLayout, loadNewAd: Boolean = true
    ) {
        try {
            if (enable && !saveValue.isAppPurchase() && adView != null) {
                adView?.let {
                    try {
                        adFrame.visibility = View.VISIBLE
                        try {
                            it.parent?.let { parent ->
                                (parent as ViewGroup).removeAllViews()
                            }
                            adFrame.removeAllViews()
                        } catch (_: Exception) {
                        }
                        adFrame.addView(it)
                        adControllerListener?.onPopulateAd(it)
                        adView = null
                        if (loadNewAd) {
                            loadBannerAd(context, enable)
                        }
                    } catch (_: Exception) {
                    }
                }
            } else {
                loadBannerAd(context, enable)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}