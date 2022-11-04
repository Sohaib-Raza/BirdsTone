package com.sound.birdstone

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.sound.birdstone.ads.bannerAds.BannerAdController
import com.sound.birdstone.helper.InternetController
import com.sound.birdstone.helper.SaveValues
import com.sound.birdstone.ads.interstitial.InterstitialController
import javax.inject.Inject

abstract class BaseActivity : LocalizationActivity() {

    @Inject
    lateinit var saveValues: SaveValues



    @Inject
    lateinit var internetController: InternetController

    @Inject
    lateinit var interstitialController: InterstitialController

    @Inject
    lateinit var bannerAdController: BannerAdController


    protected lateinit var activity: AppCompatActivity

    protected val viewModel: BirdViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
    }

}