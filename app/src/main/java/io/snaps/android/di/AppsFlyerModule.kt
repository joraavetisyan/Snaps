package io.snaps.android.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.android.appsflyer.DeepLinkProvider
import io.snaps.android.appsflyer.DeepLinkProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppsFlyerModule {

    @Binds
    @Singleton
    fun deepLinkProvider(provider: DeepLinkProviderImpl): DeepLinkProvider
}