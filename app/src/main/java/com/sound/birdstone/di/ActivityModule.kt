package com.sound.birdstone.di

import android.app.Activity
import android.content.Context
import com.sound.birdstone.databinding.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@InstallIn(ActivityComponent::class)
@Module
object ActivityModule {

    @ActivityScoped
    @Provides
    fun provideActivitySplashBinding(@ActivityContext appContext: Context) =
        ActivitySplashBinding.inflate((appContext as Activity).layoutInflater)

    @ActivityScoped
    @Provides
    fun provideActivityMainBinding(@ActivityContext appContext: Context) =
        ActivityMainBinding.inflate((appContext as Activity).layoutInflater)


    @ActivityScoped
    @Provides
    fun provideActivityRateBinding(@ActivityContext appContext: Context) =
        ActivityRateBinding.inflate((appContext as Activity).layoutInflater)

    @ActivityScoped
    @Provides
    fun provideActivitySearchBinding(@ActivityContext appContext: Context) =
        ActivitySearchBinding.inflate((appContext as Activity).layoutInflater)

    @ActivityScoped
    @Provides
    fun provideActivitySetRingtoneBinding(@ActivityContext appContext: Context) =
        ActivitySetRingtoneBinding.inflate((appContext as Activity).layoutInflater)


}