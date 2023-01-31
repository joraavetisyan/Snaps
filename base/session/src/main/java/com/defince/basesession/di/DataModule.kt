package com.defince.basesession.di

import com.defince.basesession.data.LogoutApi
import com.defince.basesession.data.RefreshApi
import com.defince.basesession.data.SessionRepository
import com.defince.basesession.data.SessionRepositoryImpl
import com.defince.coredata.network.ApiConfig
import com.defince.coredata.network.ApiService
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