package com.sound.birdstone

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.sound.birdstone.ads.interstitial.InterstitialControllerListener
import com.sound.birdstone.ads.nativeAds.BaseNativeActivity
import com.sound.birdstone.ads.openAd.AdsConstants
import com.sound.birdstone.databinding.ActivitySetRingtoneBinding
import com.sound.birdstone.databinding.DialogPermissionRequiredBinding
import com.sound.birdstone.helper.AppUtils
import com.sound.birdstone.helper.MyRingtoneManager.setAsRingtoneOrNotification
import com.sound.birdstone.helper.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SetRingtoneActivity :BaseNativeActivity() {

    @Inject
    lateinit var binding: ActivitySetRingtoneBinding

    private var soundRawId = 0
    private var type = 0
    private var soundName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding) {


            initNativeAdData(
                AdsConstants.NATIVE_IN_SET_RINGTONE,
                adsLayout.adFrame,
                true
            )

            intent.extras?.let {
                soundRawId = it.getInt("IdKey")
                soundName = it.getString("NameKey", "")
            }


            rlRingtone.setOnClickListener {
                type = RingtoneManager.TYPE_RINGTONE
                checkPermission()
            }

            rlAlarm.setOnClickListener {
                type = RingtoneManager.TYPE_ALARM
                checkPermission()
            }

            rlSmsAlert.setOnClickListener {
                type = RingtoneManager.TYPE_NOTIFICATION
                checkPermission()
            }
        }


        onBackPressedDispatcher.addCallback(
            this /* lifecycle owner */,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    interstitialController.showInterstitial(activity,
                        AdsConstants.FS_AT_BACK_OF_SET_RINGTONE, object :
                            InterstitialControllerListener {
                            override fun onAdClosed() {
                                finish()
                            }

                            override fun onAdShow() {
                            }

                        })
                }
            })
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                checkStoragePermission()
            } else {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.activity_dialog_ringtone)
                val txtCancel = dialog.findViewById<TextView>(R.id.txtDialogCancel)
                val txtSetting = dialog.findViewById<TextView>(R.id.txtDialogSetting)
                val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
                dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
                txtSetting.setOnClickListener {
                    if (!isFinishing && !isDestroyed) {
                        dialog.dismiss()
                    }
                    Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                        data = Uri.parse("package:$packageName")
                    }.also {
                        startActivity(it)
                    }
                }
                txtCancel.setOnClickListener {
                    if (!isFinishing && !isDestroyed) {
                        dialog.dismiss()
                    }
                }
                if (!isFinishing && !isDestroyed) {
                    dialog.show()
                }
            }
        } else {
            checkStoragePermission()
        }
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            requestPermissionUser()
        } else {
            setRingtone()
        }
    }

    private fun setRingtone() {
        setAsRingtoneOrNotification(
            activity, soundName, soundRawId, type
        ) { b ->

            interstitialController.showInterstitial(activity,
                AdsConstants.FS_AT_SET_RINGTONE, object :
                    InterstitialControllerListener {
                    override fun onAdClosed() {
                        if (b) {
                            val message = "$soundName.mp3 ${
                                when (type) {
                                    RingtoneManager.TYPE_RINGTONE -> {
                                        getString(R.string.ringtone)
                                    }
                                    RingtoneManager.TYPE_NOTIFICATION -> {
                                        getString(R.string.notification)

                                    }
                                    RingtoneManager.TYPE_ALARM -> {
                                        getString(R.string.alarm)
                                    }
                                    else -> {
                                        getString(R.string.ringtone)
                                    }
                                }
                            }  ${getString(R.string.set_successfully)}"

                            toast(message)

                        } else {
                            toast(getString(R.string.failed_to_set_the_ringtone))
                        }
                    }

                    override fun onAdShow() {
                    }

                })


        }
    }

    private fun requestPermissionUser() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setRingtone()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            AlertDialog.Builder(this).setTitle("Storage permission")
                .setMessage("Storage permission need for access files")
                .setNegativeButton("Cancel") { dialog: DialogInterface?, _: Int -> dialog?.dismiss() }
                .setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                    writePermissionResultLauncher.launch(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }.create().show()
        } else {
            writePermissionResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private val writePermissionResultLauncher =
        (this as ComponentActivity).registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                setRingtone()
            } else {


                DialogPermissionRequiredBinding.inflate(layoutInflater).apply {
                    val dialog = AppUtils.createDialog(activity, root)

                    goToSetting.setOnClickListener {
                        dialog.dismiss()
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    dialog.show()
                }


            }
        }


}