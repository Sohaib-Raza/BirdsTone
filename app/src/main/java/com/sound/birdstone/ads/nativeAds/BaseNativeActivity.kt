package com.sound.birdstone.ads.nativeAds

import android.view.View
import android.widget.LinearLayout
import com.sound.birdstone.BaseActivity
import com.google.android.gms.ads.nativead.NativeAd
import com.sound.birdstone.ads.AdControllerListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseNativeActivity : BaseActivity() {
    private var largeNativeAd: NativeAd? = null
    private var smallNativeAd: NativeAd? = null
    private var exitNativeAd: NativeAd? = null
    private var isAdEnabled: Boolean = false
    private var isForLarge: Boolean = false

    private var loadNewAd: Boolean = false
    private var adFrame: LinearLayout? = null

    private var isAdLoadCalled: Boolean = false
    private var isRequesting: Boolean = false

    @Inject
    lateinit var nativeAdController: NativeAdController

    @Inject
    lateinit var nativeSmallAdController: NativeSmallAdController


    fun initNativeAdData(
        isAdEnabled: Boolean, adFrame: LinearLayout, isForLarge: Boolean, loadNewAd: Boolean = true
    ) {
        this.isAdEnabled = isAdEnabled
        this.loadNewAd = loadNewAd
        this.adFrame = adFrame
        this.isForLarge = isForLarge
        isAdLoadCalled = true
        loadSingleNativeAd()
    }

    private fun destroyNativeAd() {
        isAdLoadCalled = false
        smallNativeAd?.destroy()
        smallNativeAd = null
        largeNativeAd?.destroy()
        largeNativeAd = null
        destroyExitNativeAd()
    }

    fun destroyExitNativeAd() {
        exitNativeAd?.destroy()
        exitNativeAd = null
    }

    private fun hideAdFrame() {
        adFrame?.let { it ->
            it.visibility = View.GONE
            it.removeAllViews()
        }
    }

    private fun loadSingleNativeAd() {
        if (isAdLoadCalled) {
            if (adFrame == null || !isAdEnabled || saveValues.isAppPurchase() || !internetController.isInternetConnected) {
                hideAdFrame()
            } else {
                adFrame?.let { it ->
                    if (isForLarge) {
                        if (largeNativeAd == null) {
                            if (!isRequesting) {
                                isRequesting = true
                                nativeAdController.setNativeControllerListener(object :
                                    AdControllerListener {
                                    override fun onAdLoaded() {
                                        isRequesting = false
                                        if (isFinishing || isDestroyed || isChangingConfigurations) {
                                            return
                                        }
                                        if (largeNativeAd == null) {
                                            loadSingleNativeAd()
                                        }
                                    }

                                    override fun onAdFailed() {
                                        isRequesting = false
                                        if (isFinishing || isDestroyed || isChangingConfigurations) {
                                            return
                                        }
                                        hideAdFrame()
                                    }

                                    override fun resetRequest() {
                                        isRequesting = false
                                    }

                                    override fun onPopulateAd(any: Any) {
                                        isRequesting = false
                                        if (isFinishing || isDestroyed || isChangingConfigurations) {
                                            return
                                        }
                                        nativeAdController.setNativeControllerListener(null)
                                        largeNativeAd = any as NativeAd
                                    }
                                })
                                nativeAdController.populateNativeAd(
                                    activity, isAdEnabled, it, loadNewAd
                                )
                            }
                        }
                    } else {
                        if (smallNativeAd == null) {
                            if (!isRequesting) {
                                isRequesting = true
                                nativeSmallAdController.setNativeControllerListener(object :
                                    AdControllerListener {
                                    override fun onAdLoaded() {
                                        isRequesting = false
                                        if (isFinishing || isDestroyed || isChangingConfigurations) {
                                            return
                                        }
                                        if (smallNativeAd == null) {
                                            loadSingleNativeAd()
                                        }
                                    }

                                    override fun resetRequest() {
                                        isRequesting = false
                                    }

                                    override fun onAdFailed() {
                                        isRequesting = false
                                        if (isFinishing || isDestroyed || isChangingConfigurations) {
                                            return
                                        }
                                        hideAdFrame()
                                    }

                                    override fun onPopulateAd(any: Any) {
                                        isRequesting = false
                                        if (isFinishing || isDestroyed || isChangingConfigurations) {
                                            return
                                        }
                                        nativeSmallAdController.setNativeControllerListener(null)
                                        smallNativeAd = any as NativeAd
                                    }
                                })
                                nativeSmallAdController.populateNativeAd(
                                    activity, isAdEnabled, it, loadNewAd
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun loadExitNativeAd(
        enable: Boolean, adFrame: LinearLayout, isForLarge: Boolean, loadNewAd: Boolean
    ) {
        if (!enable || saveValues.isAppPurchase() || !internetController.isInternetConnected) {
            adFrame.visibility = View.GONE
            adFrame.removeAllViews()
        } else {
            if (exitNativeAd == null) {
                nativeAdController.setNativeControllerListener(object : AdControllerListener {
                    override fun onAdLoaded() {
                        if (isFinishing || isDestroyed || isChangingConfigurations) {
                            return
                        }
                        if (exitNativeAd == null) {
                            loadExitNativeAd(enable, adFrame, isForLarge, loadNewAd)
                        }
                    }

                    override fun onAdFailed() {
                        if (isFinishing || isDestroyed || isChangingConfigurations) {
                            return
                        }
                        adFrame.visibility = View.GONE
                        adFrame.removeAllViews()
                    }

                    override fun resetRequest() {

                    }

                    override fun onPopulateAd(any: Any) {
                        if (isFinishing || isDestroyed || isChangingConfigurations) {
                            return
                        }
                        nativeAdController.setNativeControllerListener(null)
                        exitNativeAd = any as NativeAd
                    }
                })
                nativeAdController.populateNativeAd(
                    activity, enable, adFrame, loadNewAd
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadSingleNativeAd()
    }

    override fun onDestroy() {
        destroyNativeAd()
        super.onDestroy()
    }
}