package com.sound.birdstone

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.sound.birdstone.adapter.BirdAdapter
import com.sound.birdstone.ads.interstitial.InterstitialControllerListener
import com.sound.birdstone.ads.nativeAds.BaseNativeActivity
import com.sound.birdstone.ads.openAd.AdsConstants
import com.sound.birdstone.database.BirdEntity
import com.sound.birdstone.databinding.ActivitySearchBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : BaseNativeActivity() {

    private val dbData = ArrayList<BirdEntity>()

    @Inject
    lateinit var binding: ActivitySearchBinding

    private lateinit var adapter: BirdAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.searchBack.setOnClickListener {
            backPress()
        }
        binding.searchClose.setOnClickListener {
            binding.etSearch.setText("")
        }
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            text?.let {
                if (text.isEmpty()) {
                    binding.searchClose.visibility = View.GONE
                } else {
                    binding.searchClose.visibility = View.VISIBLE
                }
                filter(text.toString())
            }
        }


        adapter = BirdAdapter(
            activity,
            binding.webViewLayout,
            viewModel, saveValues, internetController, nativeAdController
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = adapter
        initNativeAdData(
            AdsConstants.NATIVE_IN_SEARCH,
            binding.adsLayout.adFrame,
            false
        )




        intent?.extras?.let { bundle ->
            if (bundle.getBoolean("isFav", false)) {
                viewModel.birdFavorite.observe(this) {
                    adapter.submitList(it)
                    dbData.addAll(it)

                }

            } else {

                viewModel.allBird.observe(this) {
                    adapter.submitList(it)
                    dbData.addAll(it)

                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPress()
            }
        })
    }

    private fun backPress() {

        interstitialController.showInterstitial(activity,
            AdsConstants.FS_AT_BACK_OF_SEARCH, object :
                InterstitialControllerListener {
                override fun onAdClosed() {
                    finish()
                }

                override fun onAdShow() {
                }

            })
    }

    private fun filter(text: String) {
        if (text.trim().isEmpty()) {
            adapter.submitList(dbData)
            return
        }
        val filteredList = ArrayList<BirdEntity>()

        for (i in 0 until dbData.size) {
            val item = dbData[i]
            if (item.birdName.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }
        adapter.submitList(filteredList)

    }


}