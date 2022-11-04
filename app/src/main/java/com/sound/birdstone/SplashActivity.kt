package com.sound.birdstone

import android.content.Intent
import android.os.Bundle
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.sound.birdstone.ads.interstitial.InterstitialControllerListener
import com.sound.birdstone.ads.nativeAds.BaseNativeActivity
import com.sound.birdstone.ads.openAd.AdsConstants
import com.sound.birdstone.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : BaseNativeActivity() {

    @Inject
    lateinit var binding: ActivitySplashBinding

    private var isMainLaunched = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.imageAnimation.imageAssetsFolder="/image"




        interstitialController.initSplashAdmob(
            activity, object : InterstitialControllerListener {
                override fun onAdClosed() {
                    launchMain()
                }

                override fun onAdShow() {
                }
            }
        )
        (AppClass.appContext as AppClass).initApp()


        nativeAdController.loadNativeAd(this, true)


        try {
            FirebaseRemoteConfig.getInstance().apply {
                val configSettings = FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build()
                setConfigSettingsAsync(configSettings)
                setDefaultsAsync(R.xml.remote_config_defaults)
                fetchAndActivate().addOnCompleteListener {
                    if (it.isSuccessful) {

                        AdsConstants.OPEN_AD_ENABLE = getBoolean("enable_app_open_ad")

                        AdsConstants.NATIVE_IN_ALL_TAB = getBoolean("native_in_all_tab")
                        AdsConstants.ALL_TAB_ITEM_COUNT = getLong("all_tab_count")

                        AdsConstants.NATIVE_IN_FAV_TAB = getBoolean("native_in_fav_tab")
                        AdsConstants.FAV_TAB_ITEM_COUNT = getLong("fav_tab_count")

                        AdsConstants.NATIVE_IN_OTHER_TAB = getBoolean("native_in_other_tab")
                        AdsConstants.OTHER_TAB_ITEM_COUNT = getLong("other_tab_count")

                        AdsConstants.NATIVE_IN_SETTING_FRAGMENT = getBoolean("native_in_setting_fragment")
                        AdsConstants.NATIVE_IN_SEARCH = getBoolean("native_in_search")
                        AdsConstants.NATIVE_IN_SET_RINGTONE = getBoolean("native_in_set_ring_tone")
                        AdsConstants.FS_AT_BOTTOM_MENU_CLICK = getBoolean("fs_at_bottom_menu_click")
                        AdsConstants.FS_AT_BACK_OF_SEARCH = getBoolean("fs_at_back_of_search")
                        AdsConstants.FS_AT_SET_RINGTONE = getBoolean("fs_at_set_ringtone")
                        AdsConstants.FS_AT_BACK_OF_SET_RINGTONE = getBoolean("fs_at_back_of_set_ringtone")
                    }
                }
            }
        } catch (_: Exception) {
        }

    }


    private fun launchMain() {

        if (!isMainLaunched) {
            isMainLaunched = true
            Intent(this, MainActivity::class.java).apply {
                startActivity(this)
            }
            finish()
        }
    }

    override fun onResume() {
        binding.imageAnimation.playAnimation()
        interstitialController.resumeAd(this, 1)
        super.onResume()

    }

    override fun onPause() {
        binding.imageAnimation.pauseAnimation()
        interstitialController.pauseAd()
        super.onPause()
    }


    override fun onDestroy() {
        super.onDestroy()
        interstitialController.destroyAd()
    }

}