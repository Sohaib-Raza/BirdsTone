package com.sound.birdstone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.sound.birdstone.databinding.ActivityRateBinding
import com.sound.birdstone.helper.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RateActivity : BaseActivity() {
    @Inject
    lateinit var binding: ActivityRateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            btnRateSubmit.setOnClickListener {
                Toast.makeText(
                    activity,
                    getString(R.string.please_rate_us),
                    Toast.LENGTH_SHORT
                ).show()
            }


            btnRateSubmit.setOnClickListener {
                if (ratingBar.rating <= 0) {

                    toast(getString(R.string.please_rate_us))

                } else if (ratingBar.rating < 4) {
                    toast(getString(R.string.thank_you_for_rate_us))
                    finish()
                } else if (ratingBar.rating >= 4) {
                    viewUrlContent(
                        "https://play.google.com/store/apps/details?id=${activity.packageName}"
                    )
                }


            }


        }

    }


    private fun viewUrlContent(url: String) {
        try {
            Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(url)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addCategory(Intent.CATEGORY_BROWSABLE)
            }.also {
                if (it.resolveActivity(packageManager) != null) {
                    startActivity(it)
                }
            }
        } catch (ignored: Exception) {
        }
    }


}