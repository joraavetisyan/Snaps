package io.snaps.android

import io.snaps.corenavigation.BottomBarFeatureProvider
import io.snaps.corenavigation.InitializationFeatureProvider
import io.snaps.corenavigation.MainFeatureProvider
import io.snaps.corenavigation.RegistrationFeatureProvider
import io.snaps.featureregistration.presentation.RegistrationFeatureProviderImpl
import io.snaps.featurebottombar.BottomBarFeatureProviderImpl
import io.snaps.featuremain.presentation.MainFeatureProviderImpl
import io.snaps.initialisation.InitializationFeatureProviderImpl
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