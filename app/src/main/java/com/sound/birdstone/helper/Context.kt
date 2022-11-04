package com.sound.birdstone.helper

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sound.birdstone.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

var DATA = 0
var ADS = 1

fun Activity.toast(s: String) {
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}

fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun ensureBackgroundThread(callback: () -> Unit) {
    if (isOnMainThread()) {
        CoroutineScope(Dispatchers.IO).launch {
            callback()
        }
    } else {
        callback()
    }
}

inline fun <reified T> MutableList<T>.makeCopy(): MutableList<T> {
    try {
        return Gson().fromJson(
            Gson().toJson(this),
            object : TypeToken<ArrayList<T>>() {}.type
        )
    } catch (e: Exception) {

    }
    return this
}

inline fun <reified T> ArrayList<T>.makeCopy(): ArrayList<T> {
    try {
        return Gson().fromJson(
            Gson().toJson(this),
            object : TypeToken<ArrayList<T>>() {}.type
        )
    } catch (e: Exception) {

    }
    return this
}
fun Context.sendUserAnalytics(message: String) {
    try {
        if (!BuildConfig.DEBUG) {
            message.trim()
            if (message.contains(" ")) {
                message.replace(" ", "_")
            }
            FirebaseAnalytics.getInstance(this).logEvent(message.trim(), Bundle())
        }
    } catch (ignored: Exception) {
    }
}