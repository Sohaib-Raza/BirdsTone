package com.sound.birdstone.ads

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.sound.birdstone.R

class AdLoadingDialog(activity: Activity?) {
    private var alertDialog1: AlertDialog? = null
    fun showAlertDialog(activity: Activity?) {
        try {
            if (alertDialog1 != null && !alertDialog1!!.isShowing && activity != null && !activity.isFinishing && !activity.isDestroyed) {
                alertDialog1?.show()
            }
        } catch (ignored: Exception) {
        }
    }

    fun dismissAlertDialog(activity: Activity?) {
        try {
            if (alertDialog1 != null && alertDialog1!!.isShowing && activity != null && !activity.isFinishing && !activity.isDestroyed) {
                alertDialog1?.dismiss()
            }
        } catch (ignored: Exception) {
        }
    }

    init {
        try {
            val builder = AlertDialog.Builder(activity)
            val v1 = LayoutInflater.from(activity).inflate(R.layout.ads_loading_dialog, null)
            builder.setView(v1)
            alertDialog1 = builder.create().apply {
                setCancelable(false)
                if (window != null) {
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
                setCancelable(false)
                setCanceledOnTouchOutside(false)
            }
        } catch (ignored: Exception) {
        }
    }
}