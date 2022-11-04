package com.sound.birdstone.ads.nativeAds

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.sound.birdstone.R
import com.sound.birdstone.ads.openAd.AdsConstants.populateUnifiedNativeAdViewForSmall
import com.sound.birdstone.constants.Constants.Companion.showToast
import com.sound.birdstone.helper.InternetController
import com.sound.birdstone.helper.SaveValues
import com.google.ads.mediation.facebook.BuildConfig
import com.google.ads.mediation.facebook.FacebookAdapter
import com.google.ads.mediation.facebook.FacebookExtras
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.sound.birdstone.ads.AdControllerListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeSmallAdController @Inject constructor(
    private val internetController: InternetController, private val saveValue: SaveValues
) {
    private var canRequestSmallAd = true
    private var smallNativeAd: NativeAd? = null
    private var adControllerListener: AdControllerListener? = null

    private var admobNativeSmallIdCount = 0

    private var admobSmallIdArray = intArrayOf(
        R.string.native_ad_common,
        R.string.native_ad_common_1,
    )

    fun setNativeControllerListener(listener: AdControllerListener?) {
        adControllerListener?.resetRequest()
        adControllerListener = listener
    }

    fun loadNativeAd(
        context: Context, enable: Boolean
    ) {
        try {
            if (enable && !saveValue.isAppPurchase() && smallNativeAd == null && internetController.isInternetConnected) {
                if (!canRequestSmallAd) {
                    return
                }
                canRequestSmallAd = false
                if (BuildConfig.DEBUG) {
                    showToast(context, "small admob ad calling")
                }

                if (admobNativeSmallIdCount == admobSmallIdArray.size) {
                    admobNativeSmallIdCount = 0
                }
                val builder = AdLoader.Builder(
                    context, context.getString(admobSmallIdArray[admobNativeSmallIdCount])
                )
                builder.forNativeAd { newNativeAd: NativeAd ->
                    canRequestSmallAd = true
                    if (BuildConfig.DEBUG) {
                        showToast(context, "small admob ad loaded")
                    }
                    smallNativeAd = newNativeAd
                    adControllerListener?.onAdLoaded()
                }

                builder.withNativeAdOptions(
                    NativeAdOptions.Builder().setVideoOptions(
                            VideoOptions.Builder().setStartMuted(true).build()
                        ).build()
                )

                val adLoader = builder.withAdListener(object : AdListener() {

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        canRequestSmallAd = true
                        smallNativeAd = null
                        if (BuildConfig.DEBUG) {
                            showToast(
                                context, "small admob load failed ==> code " + loadAdError.code
                            )
                        }
                        adControllerListener?.onAdFailed()
                    }
                }).build()
                adLoader.loadAd(
                    AdRequest.Builder().addNetworkExtrasBundle(
                            FacebookAdapter::class.java,
                            FacebookExtras().setNativeBanner(true).build()
                        ).build()
                )
                admobNativeSmallIdCount++
            }
        } catch (ignored: Exception) {
        }
    }

    fun populateNativeAd(
        context: Activity, enable: Boolean, adFrame: LinearLayout, loadNewAd: Boolean
    ) {
        if (enable && !saveValue.isAppPurchase() && smallNativeAd != null) {
            smallNativeAd?.let {
                try {
                    inflateAd(context, it, adFrame)
                    adControllerListener?.onPopulateAd(it)
                    smallNativeAd = null
                    if (loadNewAd) {
                        loadNativeAd(context, enable)
                    }
                } catch (ignored: Exception) {
                }
            }
        } else {
            loadNativeAd(context, enable)
        }
    }

    fun inflateAd(
        context: Activity, it: NativeAd, adFrame: LinearLayout
    ) {
        val adView = LayoutInflater.from(context).inflate(
            R.layout.ads_native_admob_small, null
        ) as NativeAdView
        try {
            adView.parent?.let { parent ->
                (parent as ViewGroup).removeAllViews()
            }
        } catch (_: Exception) {
        }
        populateUnifiedNativeAdViewForSmall(it, adView)
        adFrame.visibility = View.VISIBLE
        try {
            adFrame.removeAllViews()
        } catch (_: Exception) {
        }
        adFrame.addView(adView)
    }


}