package com.defince.w2e

import com.defince.corenavigation.BottomBarFeatureProvider
import com.defince.corenavigation.MainFeatureProvider
import com.defince.featurebottombar.BottomBarFeatureProviderImpl
import com.defince.featuremain.presentation.MainFeatureProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FeatureProviderModule {

    @Binds
    @Singleton
    fun bottomBarProvider(provider: BottomBarFeatureProviderImpl): BottomBarFeatureProvider

    @Binds
    @Singleton
    fun mainProvider(provider: MainFeatureProviderImpl): MainFeatureProvider
}