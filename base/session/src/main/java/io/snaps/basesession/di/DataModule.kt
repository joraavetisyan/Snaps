package io.snaps.basesession.di

import io.snaps.basesession.data.LogoutApi
import io.snaps.basesession.data.RefreshApi
import io.snaps.basesession.data.SessionRepository
import io.snaps.basesession.data.SessionRepositoryImpl
import io.snaps.coredata.network.ApiConfig
import io.snaps.coredata.network.ApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun logoutApi(config: ApiConfig) = config.serviceBuilder(LogoutApi::class.java)
        .service(ApiService.General)
        .interceptor(config.commonHeaderInterceptor)
        .interceptor(config.authenticationInterceptor)
        .build()

    @Provides
    @Singleton
    fun refreshApi(config: ApiConfig) = config.serviceBuilder(RefreshApi::class.java)
        .service(ApiService.General)
        .interceptor(config.commonHeaderInterceptor)
        .build()
}

@Module
@InstallIn(SingletonComponent::class)
interface DataBindModule {

    @Binds
    @Singleton
    fun sessionRepository(repository: SessionRepositoryImpl): SessionRepository
}