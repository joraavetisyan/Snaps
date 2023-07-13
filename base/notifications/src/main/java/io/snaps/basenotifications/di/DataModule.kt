package io.snaps.basenotifications.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basenotifications.data.NotificationsApi
import io.snaps.basenotifications.data.NotificationsRepository
import io.snaps.basenotifications.data.NotificationsRepositoryImpl
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.di.UserSessionComponent
import io.snaps.coredata.di.UserSessionComponentManager
import io.snaps.coredata.di.UserSessionScope
import io.snaps.coredata.network.ApiConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DataModule {

    @Provides
    @Singleton
    fun notificationsApi(config: ApiConfig): NotificationsApi = config
        .serviceBuilder(NotificationsApi::class.java)
        .interceptor(config.commonHeaderInterceptor)
        .interceptor(config.authenticationInterceptor)
        .build()
}

@Module
@InstallIn(UserSessionComponent::class)
internal interface DataBindModule {

    @Binds
    @UserSessionScope
    fun notificationsRepository(bind: NotificationsRepositoryImpl): NotificationsRepository
}

@EntryPoint
@InstallIn(UserSessionComponent::class)
internal interface DataBindEntryPoint {

    fun notificationsRepository(): NotificationsRepository
}

@Module
@InstallIn(SingletonComponent::class)
internal object DataBindEntryPointBridge {

    @Bridged
    @Provides
    fun notificationsRepository(
        componentManager: UserSessionComponentManager,
    ): NotificationsRepository {
        return EntryPoints
            .get(componentManager, DataBindEntryPoint::class.java)
            .notificationsRepository()
    }
}