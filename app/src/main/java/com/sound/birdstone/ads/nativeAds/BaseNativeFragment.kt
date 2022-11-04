package com.sound.birdstone.ads.nativeAds

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.sound.birdstone.fragments.BaseFragment
import com.google.android.gms.ads.nativead.NativeAd
import com.sound.birdstone.ads.AdControllerListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseNativeFragment : BaseFragment() {
    private var largeNativeAd: NativeAd? = null
    private var smallNativeAd: NativeAd? = null
    private var isAdEnabled: Boolean = false
    private var isForLarge: Boolean = false

    private var loadNewAd: Boolean = false
    private var adFrame: LinearLayout? = null
    private var imageView: ImageView? = null

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
                                        if (fragmentActivity.isFinishing || fragmentActivity.isDestroyed || fragmentActivity.isChangingConfigurations) {
                                            return
                                        }
                                        if (isDetached) {
                                            return
                                        }
                                        if (largeNativeAd == null) {
                                            loadSingleNativeAd()
                                        }
                                    }

                                    override fun onAdFailed() {
                                        isRequesting = false
                                        if (fragmentActivity.isFinishing || fragmentActivity.isDestroyed || fragmentActivity.isChangingConfigurations) {
                                            return
                                        }
                                        if (isDetached) {
                                            return
                                        }
                                        hideAdFrame()
                                    }

                                    override fun resetRequest() {
                                        isRequesting = false
                                    }

                                    override fun onPopulateAd(any: Any) {
                                        isRequesting = false
                                        if (fragmentActivity.isFinishing || fragmentActivity.isDestroyed || fragmentActivity.isChangingConfigurations) {
                                            return
                                        }
                                        if (isDetached) {
                                            return
                                        }
                                        nativeAdController.setNativeControllerListener(null)
                                        largeNativeAd = any as NativeAd
                                    }
                                })
                                nativeAdController.populateNativeAd(
                                    fragmentActivity, isAdEnabled, it, loadNewAd
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
                                        if (fragmentActivity.isFinishing || fragmentActivity.isDestroyed || fragmentActivity.isChangingConfigurations) {
                                            return
                                        }
                                        if (isDetached) {
                                            return
                                        }
                                        if (smallNativeAd == null) {
                                            loadSingleNativeAd()
                                        }
                                    }

                                    override fun onAdFailed() {
                                        isRequesting = false
                                        if (fragmentActivity.isFinishing || fragmentActivity.isDestroyed || fragmentActivity.isChangingConfigurations) {
                                            return
                                        }
                                        if (isDetached) {
                                            return
                                        }
                                        hideAdFrame()
                                    }

                                    override fun resetRequest() {
                                        isRequesting = false
                                    }

                                    override fun onPopulateAd(any: Any) {
                                        isRequesting = false
                                        if (fragmentActivity.isFinishing || fragmentActivity.isDestroyed || fragmentActivity.isChangingConfigurations) {
                                            return
                                        }
                                        if (isDetached) {
                                            return
                                        }
                                        nativeSmallAdController.setNativeControllerListener(null)
                                        smallNativeAd = any as NativeAd
                                    }
                                })
                                nativeSmallAdController.populateNativeAd(
                                    fragmentActivity, isAdEnabled, it,

                                    loadNewAd
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun hideAdFrame() {
        adFrame?.let { it ->
            it.visibility = View.GONE
            it.removeAllViews()
        }
        imageView?.visibility = View.VISIBLE
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