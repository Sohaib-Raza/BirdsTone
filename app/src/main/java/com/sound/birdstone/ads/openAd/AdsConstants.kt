package com.sound.birdstone.ads.openAd

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowMetrics
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.sound.birdstone.R

object AdsConstants {

    var INTER_SHOWING = false

    //fetch these values from firebase remote config
    var BANNER_LOADING_SHOWING = true
    var NATIVE_LOADING_SHOWING = true
    var INTERSTITIAL_LOADING = true
    var INTERSTITIAL_LOADING_TIME: Long = 1
    var SPLASH_TIME: Long = 6
    var isAppInPause = false
    var canShowOpenAd = false
    var isOpenAdShowing = false

    //remote config value
    var time_base_inter_enable = false
    var inter_home_delay_in_seconds = 0L
    var inter_home_add_in_seconds = 0

    var OPEN_AD_ENABLE = true

    var NATIVE_IN_ALL_TAB = true
    var ALL_TAB_ITEM_COUNT: Long = 8

    var NATIVE_IN_FAV_TAB = true
    var FAV_TAB_ITEM_COUNT: Long = 8

    var NATIVE_IN_OTHER_TAB = true
    var OTHER_TAB_ITEM_COUNT: Long = 10


    var  NATIVE_IN_SETTING_FRAGMENT = true
    var  NATIVE_IN_SEARCH = true
    var  NATIVE_IN_SET_RINGTONE = true

    var  FS_AT_BOTTOM_MENU_CLICK = true
    var  FS_AT_BACK_OF_SEARCH = true
    var  FS_AT_SET_RINGTONE = true
    var  FS_AT_BACK_OF_SET_RINGTONE = true


    fun getAdSize(activity: Activity): AdSize {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
            val bounds: Rect = windowMetrics.bounds
            val adWidthPixels = bounds.width()
            val density: Float = activity.resources.displayMetrics.density
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
        } else {
            val display = activity.windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val widthPixels = outMetrics.widthPixels.toFloat()
            val density = outMetrics.density
            val adWidth = (widthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
        }
    }

    fun populateUnifiedNativeAdViewLarge(
        context: Activity, nativeAd: NativeAd, adView: NativeAdView
    ) {
        val admobNative = adView.findViewById<MediaView>(R.id.ad_media)
        adView.mediaView = admobNative
        admobNative.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View?, child: View?) {
                try {
                    if (child !is ImageView) {
                        return
                    }
                    child.adjustViewBounds = true
                    child.scaleType = ImageView.ScaleType.CENTER_CROP
                } catch (_: Exception) {
                }
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {
            }

        })
        val button = adView.findViewById<Button>(R.id.ad_call_to_action)
        /*try {
            setThemeColorFilter(
                button.background, ContextCompat.getColor(
                    context,
                    getColorList()[myColorPrimary].color
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = button
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        val headLineTextView = adView.headlineView as TextView
        headLineTextView.text = nativeAd.headline
        /*if (isNightMode) {
            headLineTextView.setTextColor(Color.WHITE)
        } else {
            headLineTextView.setTextColor(Color.BLACK)
        }*/
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.GONE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            val bodyTextView = adView.bodyView as TextView
            bodyTextView.text = nativeAd.body

            /*if (isNightMode) {
                bodyTextView.setTextColor(Color.WHITE)
            } else {
                bodyTextView.setTextColor(Color.BLACK)
            }*/
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.GONE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon!!.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }
        adView.setNativeAd(nativeAd)
        val vc = nativeAd.mediaContent!!.videoController
        if (vc.hasVideoContent()) {
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
            }
        }
    }

    fun populateUnifiedNativeAdViewForSmall(
        nativeAd: NativeAd, adView: NativeAdView
    ) {
        val button = adView.findViewById<Button>(R.id.ad_call_to_action)
        try {
            /*setThemeColorFilter(
                button.background, ContextCompat.getColor(
                    context,
                    getColorList()[myColorPrimary].color
                )
            )*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = button
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        (adView.headlineView as TextView).text = nativeAd.headline
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.GONE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.GONE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon!!.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }
        adView.setNativeAd(nativeAd)
        val vc = nativeAd.mediaContent!!.videoController
        if (vc.hasVideoContent()) {
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
            }
        }
    }

}