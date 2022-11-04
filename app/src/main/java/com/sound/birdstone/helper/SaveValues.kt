package com.sound.birdstone.helper

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

const val PREFS_KEY = "Prefs"

@Singleton
class SaveValues @Inject constructor(@ApplicationContext activity: Context) {


    private val sharedPreferences = activity.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun getInt(key: String?): Int {
        return sharedPreferences.getInt(key, -1)
    }

    fun setInt(key: String?, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }


    fun getValue(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun setValue(key: String, i: Int) {
        editor.putInt(key, i).apply()
    }

    fun setAppPurchase(boolean: Boolean) {
        editor.putBoolean("app_purchase", boolean).apply()
    }

    fun isAppPurchase(): Boolean {
        return sharedPreferences.getBoolean("app_purchase", false)
    }


    fun setAppLanguage(l: String) {
        editor.putString("app_language", l).apply()
    }

    fun appLanguage(): String {
        return sharedPreferences.getString("app_language", "en") ?: "en"
    }

}
