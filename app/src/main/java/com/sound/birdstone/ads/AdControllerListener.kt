package com.sound.birdstone.ads

interface AdControllerListener {
    fun onAdLoaded()
    fun onAdFailed()
    fun resetRequest()
    fun onPopulateAd(any : Any)
}