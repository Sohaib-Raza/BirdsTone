package com.sound.birdstone.ads.nativeAds


import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.sound.birdstone.BuildConfig
import com.sound.birdstone.R
import com.sound.birdstone.ads.openAd.AdsConstants.populateUnifiedNativeAdViewLarge
import com.sound.birdstone.constants.Constants.Companion.showToast
import com.sound.birdstone.helper.InternetController
import com.sound.birdstone.helper.SaveValues
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.sound.birdstone.ads.AdControllerListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeAdController @Inject constructor(
    private val internetController: InternetController,
    private val saveValue: SaveValues
) {
    private var canRequestLargeAd = true
    private var largeNativeAd: NativeAd? = null
    private var adControllerListener: AdControllerListener? = null

    private var admobNativeLargeIdCount = 0

    private var admobLargeIdArray = intArrayOf(
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
            if (enable && !saveValue.isAppPurchase() && largeNativeAd == null &&
                internetController.isInternetConnected
            ) {
                if (!canRequestLargeAd) {
                    return
                }
                canRequestLargeAd = false
                if (BuildConfig.DEBUG) {
                    showToast(context, "large native ad calling")
                }

                if (admobNativeLargeIdCount == admobLargeIdArray.size) {
                    admobNativeLargeIdCount = 0
                }
                val builder = AdLoader.Builder(
                    context, context.getString(admobLargeIdArray[admobNativeLargeIdCount])
                )
                builder.forNativeAd { newNativeAd: NativeAd ->
                    canRequestLargeAd = true
                    if (BuildConfig.DEBUG) {
                        showToast(context, "large native ad loaded")
                    }
                    largeNativeAd = newNativeAd
                    adControllerListener?.onAdLoaded()
                }

                builder.withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .setVideoOptions(
                            VideoOptions.Builder()
                                .setStartMuted(true)
                                .build()
                        ).build()
                )

                val adLoader = builder.withAdListener(object : AdListener() {

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        canRequestLargeAd = true
                        largeNativeAd = null
                        if (BuildConfig.DEBUG) {
                            showToast(
                                context,
                                "large native load failed ==> code " + loadAdError.code
                            )
                        }
                        adControllerListener?.onAdFailed()
                    }
                }).build()
                adLoader.loadAd(AdRequest.Builder().build())
                admobNativeLargeIdCount++
            }
        } catch (ignored: Exception) {
        }
    }


    fun populateNativeAd(
        context: Activity, enable: Boolean,
        adFrame: LinearLayout, loadNewAd: Boolean = true
    ) {
        if (enable && !saveValue.isAppPurchase() && largeNativeAd != null) {
            largeNativeAd?.let {
                try {
                    inflateAd(context, it, adFrame)
                    adControllerListener?.onPopulateAd(it)
                    largeNativeAd = null
                    if (loadNewAd) {
                        loadNativeAd(context, enable)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            loadNativeAd(context, enable)
        }
    }

    fun refreshAdTheme(isNightMode: Boolean) {
        adView?.let {
            val headline = it.findViewById(R.id.ad_headline) as TextView
            val adBody = it.findViewById(R.id.ad_body) as TextView

            if (isNightMode) {
                headline.setTextColor(Color.WHITE)
            } else {
                headline.setTextColor(Color.BLACK)
            }
            if (isNightMode) {
                adBody.setTextColor(Color.WHITE)
            } else {
                adBody.setTextColor(Color.BLACK)
            }
        }
    }

    var adView: View? = null

    fun inflateAd(
        context: Activity, ad: NativeAd, adFrame: LinearLayout
    ) {
        adView = LayoutInflater.from(context).inflate(
            R.layout.ads_native_admob_full_large, null
        )
        try {
            adView?.parent?.let { pt ->
                (pt as ViewGroup).removeAllViews()
            }
        } catch (_: Exception) {
        }

        adView?.let {
            populateUnifiedNativeAdViewLarge(context, ad, it.findViewById(R.id.copy_ad))
        }
        adFrame.visibility = View.VISIBLE
        try {
            adFrame.removeAllViews()
        } catch (_: Exception) {
        }
        adFrame.addView(adView)
    }


}