package com.sound.birdstone.ads.bannerAds


import android.view.View
import android.widget.LinearLayout
import com.sound.birdstone.ads.nativeAds.BaseNativeActivity
import com.google.android.gms.ads.AdView
import com.sound.birdstone.ads.AdControllerListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseBannerActivity : BaseNativeActivity() {

    private var bannerAd: AdView? = null
    private var isAdEnabled: Boolean = false
    private var loadNewAd: Boolean = false
    private var adFrame: LinearLayout? = null

    private var isAdLoadCalled: Boolean = false
    private var isRequesting: Boolean = false

    fun initBannerData(
        isAdEnabled: Boolean, adFrame: LinearLayout, loadNewAd: Boolean = true
    ) {
        this.isAdEnabled = isAdEnabled
        this.loadNewAd = loadNewAd
        this.adFrame = adFrame
        isAdLoadCalled = true
        loadSingleBannerAd()
    }

    private fun destroyBannerAd() {
        isAdLoadCalled = false
        bannerAdController.setAdControllerListener(null)
        bannerAd?.destroy()
        bannerAd = null
    }

    private fun loadSingleBannerAd() {
        if (isAdLoadCalled) {
            if (adFrame == null || !isAdEnabled || saveValues.isAppPurchase() || !internetController.isInternetConnected) {
                adFrame?.let { it ->
                    it.visibility = View.GONE
                    it.removeAllViews()
                }
            } else {
                adFrame?.let { it ->
                    if (bannerAd == null) {
                        if (!isRequesting) {
                            isRequesting = true
                            bannerAdController.setAdControllerListener(object :
                                AdControllerListener {
                                override fun onAdLoaded() {
                                    isRequesting = false
                                    if (isFinishing || isDestroyed || isChangingConfigurations) {
                                        return
                                    }
                                    if (bannerAd == null) {
                                        loadSingleBannerAd()
                                    }
                                }

                                override fun onAdFailed() {
                                    isRequesting = false
                                    if (isFinishing || isDestroyed || isChangingConfigurations) {
                                        return
                                    }
                                    adFrame?.let { it ->
                                        it.visibility = View.GONE
                                        it.removeAllViews()
                                    }
                                }

                                override fun resetRequest() {
                                    isRequesting = false
                                }

                                override fun onPopulateAd(any: Any) {
                                    isRequesting = false
                                    if (isFinishing || isDestroyed || isChangingConfigurations) {
                                        return
                                    }
                                    bannerAd = any as AdView
                                }
                            })
                            bannerAdController.populateBannerAd(
                                activity, isAdEnabled, it, loadNewAd
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadSingleBannerAd()
        bannerAd?.resume()
    }

    override fun onPause() {
        super.onPause()
        bannerAd?.pause()
    }

    override fun onDestroy() {
        destroyBannerAd()
        super.onDestroy()
    }
}