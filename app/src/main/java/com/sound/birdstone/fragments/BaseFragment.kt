package com.sound.birdstone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.sound.birdstone.BirdViewModel
import com.sound.birdstone.ads.bannerAds.BannerAdController
import com.sound.birdstone.ads.interstitial.InterstitialController
import com.sound.birdstone.helper.InternetController
import com.sound.birdstone.helper.SaveValues
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class BaseFragment : Fragment() {


    @Inject
    lateinit var saveValues: SaveValues


    @Inject
    lateinit var internetController: InternetController

    @Inject
    lateinit var interstitialController: InterstitialController

    @Inject
    lateinit var bannerAdController: BannerAdController


    protected val viewModel: BirdViewModel by viewModels()

    lateinit var fragmentActivity: FragmentActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = requireActivity()
    }


}