package com.sound.birdstone.helper

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InternetController @Inject constructor(private val connectivityManager: ConnectivityManager) {
    val isInternetConnected: Boolean
        get() {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val network = connectivityManager.activeNetwork
                    if (network != null) {
                        val nc = connectivityManager.getNetworkCapabilities(network)
                        if (nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                            return true
                        }
                    }
                } else {
                    val networkInfo = connectivityManager.activeNetworkInfo
                    return networkInfo != null && networkInfo.isConnected && networkInfo.isAvailable
                }
            } catch (e: Exception) {
            }
            return false
        }
}