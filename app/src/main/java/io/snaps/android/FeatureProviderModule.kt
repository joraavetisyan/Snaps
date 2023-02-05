package io.snaps.android

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.corenavigation.BottomBarFeatureProvider
import io.snaps.corenavigation.CollectionFeatureProvider
import io.snaps.corenavigation.FeedFeatureProvider
import io.snaps.corenavigation.InitialisationFeatureProvider
import io.snaps.corenavigation.MainFeatureProvider
import io.snaps.corenavigation.ProfileFeatureProvider
import io.snaps.corenavigation.RegistrationFeatureProvider
import io.snaps.corenavigation.TasksFeatureProvider
import io.snaps.featurebottombar.BottomBarFeatureProviderImpl
import io.snaps.featurecollection.CollectionFeatureProviderImpl
import io.snaps.featurefeed.FeedFeatureProviderImpl
import io.snaps.featuremain.presentation.MainFeatureProviderImpl
import io.snaps.featureprofile.ProfileFeatureProviderImpl
import io.snaps.featureregistration.presentation.RegistrationFeatureProviderImpl
import io.snaps.featuretasks.TasksFeatureProviderImpl
import io.snaps.initialisation.InitialisationFeatureProviderImpl
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
    fun initializationProvider(provider: InitialisationFeatureProviderImpl): InitialisationFeatureProvider

    @Binds
    @Singleton
    fun profileFeatureProvider(provider: ProfileFeatureProviderImpl): ProfileFeatureProvider

    @Binds
    @Singleton
    fun tasksFeatureProvider(provider: TasksFeatureProviderImpl): TasksFeatureProvider

    @Binds
    @Singleton
    fun collectionFeatureProvider(provider: CollectionFeatureProviderImpl): CollectionFeatureProvider

    @Binds
    @Singleton
    fun feedFeatureProvider(provider: FeedFeatureProviderImpl): FeedFeatureProvider
}