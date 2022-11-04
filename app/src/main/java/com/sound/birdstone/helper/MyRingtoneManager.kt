package com.sound.birdstone.helper


import android.app.Activity
import android.content.*
import android.media.MediaScannerConnection
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import kotlinx.coroutines.*
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


object MyRingtoneManager {
    fun setAsRingtoneOrNotification(
        activity: Activity, soundName: String, soundId: Int, ringtoneType: Int,
        callback: (Boolean) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cw = ContextWrapper(activity)
                val directory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    cw.getDir("audio", Context.MODE_PRIVATE)
                } else {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES)
                }
                val k = File(directory, "$soundName.ogg")
                val input = activity.resources.openRawResource(soundId)
                val contentResolver = activity.contentResolver
                withContext(Dispatchers.IO) {
                    val out = FileOutputStream(k)
                    val buff = ByteArray(1024)
                    var read: Int
                    try {
                        while (input.read(buff).also { read = it } > 0) {
                            out.write(buff, 0, read)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        input.close()
                        out.close()
                    }
                }
                MediaScannerConnection.scanFile(
                    activity,
                    arrayOf(k.absolutePath),
                    null
                ) { _: String?, _: Uri? -> }
                val tempUri = FileProvider.getUriForFile(
                    activity, activity.applicationContext.packageName.toString() + ".provider", k
                )
                val values = ContentValues()
                val mimeTypeMap = getMimeType(contentResolver, tempUri)
                values.put(MediaStore.MediaColumns.TITLE, k.name)
                values.put(MediaStore.MediaColumns.MIME_TYPE, mimeTypeMap)
                values.put(MediaStore.MediaColumns.SIZE, k.length())

                when (ringtoneType) {
                    RingtoneManager.TYPE_RINGTONE -> {
                        values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
                    }
                    RingtoneManager.TYPE_ALARM -> {
                        values.put(MediaStore.Audio.Media.IS_ALARM, true)
                    }
                    RingtoneManager.TYPE_NOTIFICATION -> {
                        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true)
                    }
                }
                val newUri =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentResolver.insert(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values
                        )
                    } else {
                        values.put(MediaStore.MediaColumns.DATA, k.absolutePath)
                        val uri = MediaStore.Audio.Media.getContentUriForPath(k.absolutePath)
                        delay(200)
                        val cursor = contentResolver.query(
                            uri!!, null, MediaStore.MediaColumns.DATA + "=?",
                            arrayOf(k.absolutePath), null
                        )
                        if (cursor == null) {
                            null
                        } else
                            if (cursor.moveToFirst() && cursor.count > 0) {
                                val id = cursor.getString(0)
                                cursor.close()
                                contentResolver.update(
                                    uri, values, MediaStore.MediaColumns.DATA + "=?",
                                    arrayOf(k.absolutePath)
                                )
                                ContentUris.withAppendedId(uri, id.toLong())
                            } else {
                                cursor.close()
                                null
                            }
                    }
                try {
                    newUri.let {
                        if (it == null) {
                            withContext(Dispatchers.Main) {
                                callback.invoke(false)
                            }
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                contentResolver.openOutputStream(it)?.use { os ->
                                    val size = k.length()
                                    val bytes = ByteArray(size.toInt())
                                    try {
                                        val buf = BufferedInputStream(FileInputStream(k))
                                        buf.read(bytes, 0, bytes.size)
                                        buf.close()
                                        os.write(bytes)
                                        os.close()
                                        os.flush()
                                    } catch (_: Exception) {
                                    }
                                }
                            }
                            RingtoneManager.setActualDefaultRingtoneUri(
                                activity, ringtoneType, it
                            )
                            Settings.System.putString(
                                contentResolver, when (ringtoneType) {
                                    RingtoneManager.TYPE_ALARM -> {
                                        Settings.System.ALARM_ALERT
                                    }
                                    RingtoneManager.TYPE_RINGTONE -> {
                                        Settings.System.RINGTONE
                                    }
                                    else -> {
                                        Settings.System.NOTIFICATION_SOUND
                                    }
                                },
                                newUri.toString()
                            )

                            withContext(Dispatchers.Main) {
                                callback.invoke(true)
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        callback.invoke(false)
                    }
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.invoke(false)
                }
                e.printStackTrace()
            }
        }
    }

    private fun getMimeType(contentResolver: ContentResolver, uri: Uri): String {
        val mimeType: String? = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            contentResolver.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
        }
        return mimeType ?: "audio/mp3"
    }


}