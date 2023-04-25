package io.snaps.android

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.corenavigation.BottomBarFeatureProvider
import io.snaps.corenavigation.CollectionFeatureProvider
import io.snaps.corenavigation.CreateFeatureProvider
import io.snaps.corenavigation.FeedFeatureProvider
import io.snaps.corenavigation.InitializationFeatureProvider
import io.snaps.corenavigation.SearchFeatureProvider
import io.snaps.corenavigation.ProfileFeatureProvider
import io.snaps.corenavigation.ReferralFeatureProvider
import io.snaps.corenavigation.RegistrationFeatureProvider
import io.snaps.corenavigation.TasksFeatureProvider
import io.snaps.corenavigation.WalletConnectFeatureProvider
import io.snaps.corenavigation.WalletFeatureProvider
import io.snaps.featurebottombar.BottomBarFeatureProviderImpl
import io.snaps.featurecollection.CollectionFeatureProviderImpl
import io.snaps.featurecreate.CreateFeatureProviderImpl
import io.snaps.featurefeed.FeedFeatureProviderImpl
import io.snaps.featureinitialization.InitializationFeatureProviderImpl
import io.snaps.featureprofile.ProfileFeatureProviderImpl
import io.snaps.featurereferral.ReferralFeatureProviderImpl
import io.snaps.featureregistration.presentation.RegistrationFeatureProviderImpl
import io.snaps.featuresearch.SearchFeatureProviderImpl
import io.snaps.featuretasks.TasksFeatureProviderImpl
import io.snaps.featurewallet.WalletFeatureProviderImpl
import io.snaps.featurewalletconnect.WalletConnectFeatureProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FeatureProviderModule {

    @Binds
    @Singleton
    fun registrationProvider(provider: RegistrationFeatureProviderImpl): RegistrationFeatureProvider

    @Binds
    @Singleton
    fun walletConnectFeatureProvider(provider: WalletConnectFeatureProviderImpl): WalletConnectFeatureProvider

    @Binds
    @Singleton
    fun initializationProvider(provider: InitializationFeatureProviderImpl): InitializationFeatureProvider

    @Binds
    @Singleton
    fun bottomBarProvider(provider: BottomBarFeatureProviderImpl): BottomBarFeatureProvider

    @Binds
    @Singleton
    fun feedFeatureProvider(provider: FeedFeatureProviderImpl): FeedFeatureProvider

    @Binds
    @Singleton
    fun searchFeatureProvider(provider: SearchFeatureProviderImpl): SearchFeatureProvider

    @Binds
    @Singleton
    fun tasksFeatureProvider(provider: TasksFeatureProviderImpl): TasksFeatureProvider

    @Binds
    @Singleton
    fun collectionFeatureProvider(provider: CollectionFeatureProviderImpl): CollectionFeatureProvider

    @Binds
    @Singleton
    fun profileFeatureProvider(provider: ProfileFeatureProviderImpl): ProfileFeatureProvider

    @Binds
    @Singleton
    fun walletFeatureProvider(provider: WalletFeatureProviderImpl): WalletFeatureProvider

    @Binds
    @Singleton
    fun createFeatureProvider(provider: CreateFeatureProviderImpl): CreateFeatureProvider

    @Binds
    @Singleton
    fun referralFeatureProvider(provider: ReferralFeatureProviderImpl): ReferralFeatureProvider
}