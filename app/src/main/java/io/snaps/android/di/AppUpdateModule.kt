package io.snaps.android.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.android.appupdate.AppUpdateProvider
import io.snaps.android.appupdate.AppUpdateProviderImpl
import io.snaps.android.appupdate.UpdateRouter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppUpdateModule {

    @Binds
    @Singleton
    fun appUpdateProvider(provider: AppUpdateProviderImpl): AppUpdateProvider

    @Binds
    @Singleton
    fun updateRouter(provider: AppUpdateProvider): UpdateRouter
}