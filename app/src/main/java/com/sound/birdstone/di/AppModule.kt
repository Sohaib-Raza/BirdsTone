package com.sound.birdstone.di

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import androidx.room.Room
import com.sound.birdstone.database.BirdDao
import com.sound.birdstone.database.DatabaseHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Singleton
    @Provides
    fun provideConnectivityManager(@ApplicationContext appContext: Context): ConnectivityManager =
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    @Singleton
    @Provides
    fun provideHandler(): Handler =
        Handler(Looper.getMainLooper())

    @Singleton
    @Provides
    fun provideActivityManager(@ApplicationContext appContext: Context): ActivityManager =
        appContext.getSystemService(WallpaperService.ACTIVITY_SERVICE) as ActivityManager

    @Singleton
    @Provides
    fun provideBirdDao(db: DatabaseHelper): BirdDao =
        db.birdDao()

    @Singleton
    @Provides
    fun provideDatabaseHelper(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext, DatabaseHelper::class.java, "bird_ringtone.db"
        ).build()
}