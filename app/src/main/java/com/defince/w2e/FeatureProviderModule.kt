package com.defince.w2e

import com.defince.corenavigation.BottomBarFeatureProvider
import com.defince.corenavigation.InitializationFeatureProvider
import com.defince.corenavigation.MainFeatureProvider
import com.defince.corenavigation.RegistrationFeatureProvider
import com.defince.featureregistration.presentation.RegistrationFeatureProviderImpl
import com.defince.featurebottombar.BottomBarFeatureProviderImpl
import com.defince.featuremain.presentation.MainFeatureProviderImpl
import com.defince.initialisation.InitializationFeatureProviderImpl
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

    @Binds
    @Singleton
    fun registrationProvider(provider: RegistrationFeatureProviderImpl): RegistrationFeatureProvider

    @Binds
    @Singleton
    fun initializationProvider(provider: InitializationFeatureProviderImpl): InitializationFeatureProvider
}