package com.sound.birdstone

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.sound.birdstone.databinding.ActivityPrivacyBinding

class PrivacyActivity : LocalizationActivity() {
    private lateinit var binding: ActivityPrivacyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.web.loadUrl("https://sites.google.com/view/wallpaperandringtone/home")
        binding.web.setBackgroundColor(Color.TRANSPARENT)
        binding.web.settings.javaScriptEnabled = true
        binding.web.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                if (progress > 50) binding.progressView.visibility = View.GONE
            }
        }

    }
}