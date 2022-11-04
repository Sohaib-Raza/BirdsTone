package com.sound.birdstone.constants

import android.content.Context
import android.widget.Toast
import com.sound.birdstone.AppClass

open class Constants {
    companion object {
        fun showToast(appClass: AppClass, s: String) {
            Toast.makeText(
                appClass, s, Toast.LENGTH_SHORT
            ).show()
        }

        fun showToast(context: Context, s: String) {
            Toast.makeText(
                context, s, Toast.LENGTH_SHORT
            ).show()
        }

        var url_wikipedia = "https://en.m.wikipedia.org/wiki/";
    }
}