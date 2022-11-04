package com.sound.birdstone.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sound.birdstone.PrivacyActivity
import com.sound.birdstone.RateActivity
import com.sound.birdstone.ads.nativeAds.BaseNativeFragment
import com.sound.birdstone.ads.openAd.AdsConstants
import com.sound.birdstone.databinding.FragmentSettingBinding
import com.sound.birdstone.helper.sendUserAnalytics


class SettingFragment : BaseNativeFragment() {
    private lateinit var binding: FragmentSettingBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        with(binding) {

            initNativeAdData(
                AdsConstants.NATIVE_IN_SETTING_FRAGMENT,
                adsLayout.adFrame,
                false
            )


            moreApp.setOnClickListener {
                openPlayStoreLink("https://play.google.com/store/apps/dev?id=8787394910932261371")
            }
            shareLayout.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "")
                val data =
                    " https://play.google.com/store/apps/details?id=${fragmentActivity.packageName}"
                shareIntent.putExtra(Intent.EXTRA_TEXT, data)
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            }
            rateStars.setOnClickListener {
                val intent = Intent(fragmentActivity, RateActivity::class.java)
                startActivity(intent)
            }
            privacy.setOnClickListener {
                val intent = Intent(fragmentActivity, PrivacyActivity::class.java)
                startActivity(intent)
            }

            liveWallpaper.setOnClickListener {
                fragmentActivity.sendUserAnalytics("settings_fragment_live_wallpaper")

                openPlayStoreLink("https://play.google.com/store/apps/details?id=com.livewallpapers.live.wallpaper.hd")
            }
            iphoneWallpaper.setOnClickListener {
                fragmentActivity.sendUserAnalytics("settings_fragment_iphone_wallpaper")
                openPlayStoreLink("https://play.google.com/store/apps/details?id=com.iphone.wallpapers.wallpapers.for.iphone")
            }
            natureSound.setOnClickListener {
                fragmentActivity.sendUserAnalytics("settings_fragment_nature_sound")
                openPlayStoreLink("https://play.google.com/store/apps/details?id=com.naturesounds.sleepsound.relaxtionsounds")
            }

        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val liveWallpaper =
            appInstalledOrNot("com.livewallpapers.live.wallpaper.hd")
        val iphoneWallpaper =
            appInstalledOrNot("com.iphone.wallpapers.wallpapers.for.iphone")
        val natureSound =
            appInstalledOrNot("com.naturesounds.sleepsound.relaxtionsounds")

        if (natureSound) {
            binding.natureSound.visibility = View.GONE
        } else {
            binding.natureSound.visibility = View.VISIBLE
        }

        if (iphoneWallpaper) {
            binding.iphoneWallpaper.visibility = View.GONE
        } else {
            binding.iphoneWallpaper.visibility = View.VISIBLE
        }

        if (liveWallpaper) {
            binding.liveWallpaper.visibility = View.GONE
        } else {
            binding.liveWallpaper.visibility = View.VISIBLE
        }

        if (iphoneWallpaper && liveWallpaper && natureSound) {
            binding.moreApp.visibility = View.GONE
        } else {
            binding.moreApp.visibility = View.VISIBLE
        }

    }

    private fun openPlayStoreLink(link: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
        } catch (_: ActivityNotFoundException) {
        }
    }

    private fun appInstalledOrNot(s: String): Boolean {
        try {
            fragmentActivity.packageManager.getPackageInfo(s, PackageManager.GET_ACTIVITIES)
            return true
        } catch (_: PackageManager.NameNotFoundException) {
        }
        return false
    }
}