package com.sound.birdstone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.LinearLayoutManager
import com.sound.birdstone.adapter.BirdAdapter
import com.sound.birdstone.ads.nativeAds.BaseNativeFragment
import com.sound.birdstone.ads.openAd.AdsConstants.ALL_TAB_ITEM_COUNT
import com.sound.birdstone.ads.openAd.AdsConstants.FAV_TAB_ITEM_COUNT
import com.sound.birdstone.ads.openAd.AdsConstants.NATIVE_IN_ALL_TAB
import com.sound.birdstone.ads.openAd.AdsConstants.NATIVE_IN_FAV_TAB
import com.sound.birdstone.ads.openAd.AdsConstants.NATIVE_IN_OTHER_TAB
import com.sound.birdstone.ads.openAd.AdsConstants.OTHER_TAB_ITEM_COUNT
import com.sound.birdstone.appdata.BirdCategory
import com.sound.birdstone.database.BirdEntity
import com.sound.birdstone.databinding.FragmentFarmBinding
import com.sound.birdstone.helper.AppUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CategoryFragment : BaseNativeFragment() {

    @Inject
    lateinit var binding: FragmentFarmBinding

//    private var list = ArrayList<BirdEntity>()

    private lateinit var adapter: BirdAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BirdAdapter(
            fragmentActivity,
            binding.webViewLayout,
            viewModel,
            saveValues, internetController, nativeAdController
        )
        binding.apply {
            webViewLayout.webview.settings.javaScriptEnabled = true
            webViewLayout.webview.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    webViewLayout.progressBar.visibility = View.GONE
                }
            }
            webViewLayout.closeWebview.setOnClickListener {
                webViewLayout.webview.loadUrl("about:blank");
                webViewLayout.root.visibility = View.GONE
            }
        }

        arguments?.getInt("categoryId")?.let { categoryId ->


            binding.recyclerView.layoutManager = LinearLayoutManager(fragmentActivity)
            binding.recyclerView.adapter = adapter

            when (categoryId) {
                BirdCategory.ALL -> {
                    viewModel.allBird.observe(viewLifecycleOwner) {
                        it as ArrayList

                        if (it.isNotEmpty() && NATIVE_IN_ALL_TAB)
                            addAdsInList(it, ALL_TAB_ITEM_COUNT)

                        adapter.submitList(it)
                    }
                }
                BirdCategory.FAV -> {
                    viewModel.birdFavorite.observe(viewLifecycleOwner) {
                        it as ArrayList

                        if (it.isNotEmpty() && NATIVE_IN_FAV_TAB)
                            addAdsInList(it, FAV_TAB_ITEM_COUNT)


                        adapter.submitList(it)


                        if (it.isEmpty()) {
                            binding.emptyLayout.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        } else {
                            binding.emptyLayout.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE

                        }

                    }
                }
                else -> {
                    viewModel.birdTypeData(categoryId).observe(viewLifecycleOwner) {

                        it as ArrayList

                        if (it.isNotEmpty() && NATIVE_IN_OTHER_TAB) {
                            if (categoryId == 3 || categoryId == 5 || categoryId == 1) {
                                addAdsInList(it, OTHER_TAB_ITEM_COUNT)
                            }
                        }

                        adapter.submitList(it)

                    }
                }
            }
        }

    }

    private fun addAdsInList(it: ArrayList<BirdEntity>, count: Long) {

        for (i in 0 until it.size) {
            if (i == 0) {
                it.add(1, AppUtils.getAdsItem())
            } else if (count > 0 && i % count == 0L) {
                it.add(i - 1, AppUtils.getAdsItem())
            }
        }

    }


    override fun onPause() {
        super.onPause()
        binding.apply {
            webViewLayout.webview.loadUrl("about:blank");
            webViewLayout.root.visibility = View.GONE
        }
        adapter.releaseMediaPlayer()
    }


}