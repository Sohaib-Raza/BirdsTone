package com.sound.birdstone.di

import android.app.Activity
import android.content.Context
import com.sound.birdstone.databinding.FragmentFarmBinding
import com.sound.birdstone.databinding.FragmentHomeBinding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped

@InstallIn(FragmentComponent::class)
@Module
object FragmentModule {


    @FragmentScoped
    @Provides
    fun provideFragmentHomeBinding(@ActivityContext appContext: Context) =
        FragmentHomeBinding.inflate((appContext as Activity).layoutInflater)

   @FragmentScoped
    @Provides
    fun provideFragmentFarmBinding(@ActivityContext appContext: Context) =
       FragmentFarmBinding.inflate((appContext as Activity).layoutInflater)


}