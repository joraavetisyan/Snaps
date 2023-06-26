package io.snaps.basesettings.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basesettings.data.SettingsApi
import io.snaps.basesettings.data.SettingsRepository
import io.snaps.basesettings.data.SettingsRepositoryImpl
import io.snaps.coredata.network.ApiConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun settingsApi(config: ApiConfig): SettingsApi = config
            .serviceBuilder(SettingsApi::class.java)
            .interceptor(config.commonHeaderInterceptor)
            .interceptor(config.authenticationInterceptor)
            .build()
}

@Module
@InstallIn(SingletonComponent::class)
interface DataBindModule {

    @Binds
    @Singleton
    fun settingsRepository(bind: SettingsRepositoryImpl): SettingsRepository
}