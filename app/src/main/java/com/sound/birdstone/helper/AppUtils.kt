package com.sound.birdstone.helper

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.sound.birdstone.database.BirdEntity

class AppUtils {

    companion object {
        fun createDialog(activity: Activity, view: View): Dialog {
            val width = (activity.resources.displayMetrics.widthPixels * 0.85).toInt()
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.setContentView(view)
            dialog.window!!.setLayout(
                width,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            return dialog
        }

        fun getAdsItem(): BirdEntity {

            val birdEntity = BirdEntity("", "", "", -1)
            birdEntity.itemType = ADS
            return birdEntity
        }

        fun getResIdByResName(context: Context, resName: String, resType: String): Int {
            try {
                return context.resources.getIdentifier(resName, resType, context.packageName)
            } catch (ignored: Exception) {
            }
            return 0
        }

    }
}