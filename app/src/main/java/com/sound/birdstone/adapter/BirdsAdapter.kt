package com.sound.birdstone.adapter

import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chibde.visualizer.BarVisualizer
import com.sound.birdstone.BirdViewModel
import com.sound.birdstone.R
import com.sound.birdstone.SetRingtoneActivity
import com.sound.birdstone.ads.AdControllerListener
import com.sound.birdstone.ads.nativeAds.NativeAdController
import com.sound.birdstone.ads.openAd.AdsConstants
import com.sound.birdstone.constants.Constants
import com.sound.birdstone.database.BirdEntity
import com.sound.birdstone.databinding.AdapterAdsLayoutBinding
import com.sound.birdstone.databinding.BirdsSingleRowBinding
import com.sound.birdstone.databinding.WebViewLayoutBinding
import com.sound.birdstone.helper.ADS
import com.sound.birdstone.helper.InternetController
import com.sound.birdstone.helper.SaveValues
import com.sound.birdstone.helper.toast

class BirdAdapter(
    var activity: FragmentActivity,
    var webViewLayout: WebViewLayoutBinding,
    var viewModel: BirdViewModel,
    var saveValue: SaveValues,
    val internetController: InternetController,
    val nativeAdController: NativeAdController,

    ) :
    ListAdapter<BirdEntity, RecyclerView.ViewHolder>(COMPARATOR) {

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<BirdEntity>() {
            override fun areItemsTheSame(oldItem: BirdEntity, newItem: BirdEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BirdEntity, newItem: BirdEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    private var mp = MediaPlayer()
    var playingHolder: MyViewHolder? = null
    var currentPlayingPosition = -1
    var slecteditem = -1
    var height = activity.resources.getDimension(com.intuit.sdp.R.dimen._150sdp).toInt()
    private val glide = Glide.with(activity)


    private fun pausePlayer() {

        if (mp.isPlaying) {
            mp.pause()
        }
        updatePlayingView()
    }

    private fun hideView() {
        val temp = slecteditem
        slecteditem = -1
        notifyItemChanged(temp)
        notifyItemChanged(slecteditem)

    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ADS) {
            val binding =
                AdapterAdsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            return MyAddsViewHolder(binding)
        }

        val binding =
            BirdsSingleRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        val birdEntity = getItem(position)

        if (birdEntity.itemType == ADS) {
            (holder as MyAddsViewHolder).apply {
                loadNativeAd()
            }
        } else {
            (holder as MyViewHolder).binding.apply {

                glide.load(birdEntity.getImgResId(activity)).into(birdImageView)


                birdName.text = birdEntity.birdName


                if (position == currentPlayingPosition) {
                    playingHolder = holder
                    updatePlayingView()
                } else {
                    updateNonPlayingView(holder)
                }


                if (birdEntity.bird_favorite == 0) {
                    favImageView.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                } else {
                    favImageView.setImageResource(R.drawable.ic_baseline_favorite_24)
                }

            }
        }


    }

    private fun playSound(birdSound: Int) {
        mp = MediaPlayer.create(activity, birdSound)
        mp.setOnCompletionListener {
            releaseMediaPlayer()
        }
        mp.start()

    }

    fun releaseMediaPlayer() {
        if (null != playingHolder) {
            updateNonPlayingView(playingHolder)
        }
        try {
            mp.stop()
            mp.release()

        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

        currentPlayingPosition = -1
    }

    private fun updatePlayingView() {

        playingHolder?.let {

            if (mp.isPlaying)
                it.binding.playPauseImageView.setImageResource(R.drawable.pause_btn)
            else
                it.binding.playPauseImageView.setImageResource(R.drawable.play_btn)
            barVisualization(it.binding.visualizerBar)
            it.binding.visualizerBar.visibility = View.VISIBLE
        }

    }

    private fun updateNonPlayingView(holder: MyViewHolder?) {
        holder?.let {
            it.binding.playPauseImageView.setImageResource(R.drawable.play_btn)
            it.binding.visualizerBar.visibility = View.GONE
        }

    }


    private fun barVisualization(barVisualizer: BarVisualizer) {
        try {
            barVisualizer.visibility = View.VISIBLE
            barVisualizer.setColor(ContextCompat.getColor(activity, R.color.visulizar_color))
            barVisualizer.setDensity(60f)
            barVisualizer.setPlayer(mp.audioSessionId)
        } catch (e: Exception) {
        }
    }

    inner class MyViewHolder(val binding: BirdsSingleRowBinding) :
        RecyclerView.ViewHolder(binding.root) {


        init {

            binding.apply {
                favImageView.setOnClickListener {

                    val adapterPosition = adapterPosition
                    if (adapterPosition == -1)
                        return@setOnClickListener

                    val birdEntity = getItem(adapterPosition)


                    if (birdEntity.bird_favorite == 0) {
                        viewModel.update(1, birdEntity.id)
                        favImageView.setImageResource(R.drawable.ic_baseline_favorite_24)
                        birdEntity.bird_favorite = 1
                        Toast.makeText(activity, activity.getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show()

                    } else {
                        viewModel.update(0, birdEntity.id)

                        favImageView.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                        birdEntity.bird_favorite = 0
                        releaseMediaPlayer()
                        Toast.makeText(activity, activity.getString(R.string.remove_from_favorites), Toast.LENGTH_SHORT)
                            .show()

                    }
                    notifyItemChanged(adapterPosition)
                }




                ringtoneImageView.setOnClickListener {

                    val adapterPosition = adapterPosition
                    if (adapterPosition == -1)
                        return@setOnClickListener
                    val birdEntity = getItem(adapterPosition)

                    Intent(activity, SetRingtoneActivity::class.java).apply {
                        putExtra("IdKey", birdEntity.getSoundResId(activity))
                        putExtra("NameKey", birdEntity.birdName)
                    }.also {
                        activity.startActivity(it)
                    }

                }

                wikiPidiaImageview.setOnClickListener {

                    if (!internetController.isInternetConnected){
                        activity.toast(activity.getString(R.string.no_internet_conncetion))
                        return@setOnClickListener
                    }

                    val adapterPosition = adapterPosition
                    if (adapterPosition != -1){

                        hideView()

                        val birdEntity = getItem(adapterPosition)
                        webViewLayout.root.visibility = View.VISIBLE

                        webViewLayout.webview.loadUrl(Constants.url_wikipedia + birdEntity.birdName)
                    }





                }

                birdImageView.setOnClickListener {

                    val adapterPosition = adapterPosition
                    if (adapterPosition == -1)
                        return@setOnClickListener
                    hideView()

                    val birdEntity = getItem(adapterPosition)

                    if (adapterPosition == currentPlayingPosition) {
                        if (mp.isPlaying) {
                            mp.pause()
                        } else {
                            mp.start()
                        }
                    } else {
                        currentPlayingPosition = adapterPosition
                        if (null != playingHolder) {
                            updateNonPlayingView(playingHolder)
                        }
                        mp.release()
                        playingHolder = this@MyViewHolder
                        playSound(birdEntity.getSoundResId(activity))
                    }
                    updatePlayingView()
                }


            }


        }

    }


    inner class MyAddsViewHolder(val binding: AdapterAdsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var largeNativeAd: Any
        fun loadNativeAd() {
            if (saveValue.isAppPurchase() || !internetController.isInternetConnected) {
                if (activity.isFinishing || activity.isDestroyed || activity.isChangingConfigurations) {
                    return
                }
                binding.adFrame.removeAllViews()
                binding.adFrame.visibility = View.GONE
            } else {
                if (!::largeNativeAd.isInitialized) {
                    nativeAdController.setNativeControllerListener(object :
                        AdControllerListener {
                        override fun onAdLoaded() {
                            if (activity.isFinishing || activity.isDestroyed || activity.isChangingConfigurations) {
                                return
                            }
                            if (!::largeNativeAd.isInitialized) {
                                loadNativeAd()
                            }
                        }

                        override fun onAdFailed() {
                            if (activity.isFinishing || activity.isDestroyed || activity.isChangingConfigurations) {
                                return
                            }
                            binding.adFrame.removeAllViews()
                            binding.adFrame.visibility = View.GONE
                        }

                        override fun resetRequest() {
                        }

                        override fun onPopulateAd(any: Any) {
                            if (activity.isFinishing || activity.isDestroyed || activity.isChangingConfigurations) {
                                return
                            }
                            nativeAdController.setNativeControllerListener(null)
                            largeNativeAd = any
                        }
                    })



                    nativeAdController.populateNativeAd(
                        activity,
                        AdsConstants.NATIVE_IN_ALL_TAB,
                        binding.adFrame
                    )

                }
            }
        }

    }
}