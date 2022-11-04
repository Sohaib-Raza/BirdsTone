package com.sound.birdstone

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.akexorcist.localizationactivity.ui.LocalizationApplication
import com.sound.birdstone.helper.InternetController
import com.sound.birdstone.helper.SaveValues
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.sound.birdstone.ads.openAd.AdsConstants
import com.sound.birdstone.ads.openAd.AppOpenManager
import com.sound.birdstone.constants.Constants
import dagger.hilt.android.HiltAndroidApp
import java.util.*
import javax.inject.Inject


@HiltAndroidApp
class AppClass : LocalizationApplication(), Application.ActivityLifecycleCallbacks {

    var currentActivity: Activity? = null

    @Inject
    lateinit var saveValue: SaveValues

    @Inject
    lateinit var internetController: InternetController

    private var isAppOpenAdInitialized = false
    private var isAppInitialized = false

    companion object {
        var appContext: Context? = null
    }

    override fun getDefaultLanguage(context: Context): Locale {
        return Locale.getDefault()
    }


    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    fun initApp() {
        if (!isAppInitialized) {
            isAppInitialized = true
            try {
                FirebaseApp.initializeApp(this)
            } catch (_: Exception) {
            }
            try {
                MobileAds.initialize(this)
            } catch (_: Exception) {
            }
            try {
                AudienceNetworkAds.initialize(this)
            } catch (_: Exception) {
            }

            try {
                registerActivityLifecycleCallbacks(this)
            } catch (_: Exception) {
            }
        }
    }

       fun initializeOpenAd() {
           if (!saveValue.isAppPurchase()) {
               if (AdsConstants.OPEN_AD_ENABLE) {
                   if (!isAppOpenAdInitialized) {
                       isAppOpenAdInitialized = true
                       AppOpenManager(this, internetController, saveValue)
                   }
               }
           }
       }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
        handleCurrentActivity()
    }

    private fun handleCurrentActivity() {
        /*     canShowOpenAd = (currentActivity !is SplashActivity
                     && currentActivity !is PrivacyPolicyActivity
                     && currentActivity !is AdActivity && currentActivity !is AudienceNetworkActivity)
     */
    }

    override fun onActivityResumed(activity: Activity) {
//        isAppInPause = false
        currentActivity = activity
        handleCurrentActivity()
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {
//        isAppInPause = true
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
//        canShowOpenAd = true
    }
}