package com.sound.birdstone.database

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.sound.birdstone.helper.AppUtils.Companion.getResIdByResName
import com.sound.birdstone.helper.DATA
import java.io.Serializable


@Entity(tableName = "Bird")
class BirdEntity(

    @ColumnInfo(name = "bird_name")
    val birdName: String,

    @ColumnInfo(name = "bird_sound")
    val birdSound: String,

    @ColumnInfo(name = "bird_image")
    val birdImage: String,

    @ColumnInfo(name = "bird_type")
    val birdType: Int,


    ) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "bird_favorite")
    var bird_favorite: Int = 0


    @Ignore
    var itemType = DATA


    fun getSoundResId(context: Context): Int {
        try {
            return getResIdByResName(context, birdSound, "raw")
        } catch (ignored: Exception) {
        }
        return 0
    }

    fun getImgResId(context: Context): Int {
        try {
            return getResIdByResName(context, birdImage, "drawable")
        } catch (ignored: Exception) {
        }
        return 0
    }
}

