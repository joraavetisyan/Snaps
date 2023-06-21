package io.snaps.android.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.android.appsflyer.DeepLinkSource
import io.snaps.android.appsflyer.DeepLinkSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppsFlyerModule {

    @Binds
    @Singleton
    fun deepLinkSource(bind: DeepLinkSourceImpl): DeepLinkSource
}