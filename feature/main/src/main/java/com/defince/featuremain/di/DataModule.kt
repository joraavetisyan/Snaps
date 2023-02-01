package com.defince.featuremain.di

import com.defince.coredata.network.ApiConfig
import com.defince.coredata.network.ApiService
import com.defince.featuremain.data.MainHeaderHandler
import com.defince.featuremain.data.MainHeaderHandlerImplDelegate
import com.defince.featuremain.data.StubApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class DataModule {

    @Provides
    @ViewModelScoped
    fun stubApi(config: ApiConfig) = config.serviceBuilder(StubApi::class.java)
        .service(ApiService.General)
        .interceptor(config.authenticationInterceptor)
        .build()
}

@Module
@InstallIn(ViewModelComponent::class)
interface DataBindModule {

    @Binds
    @ViewModelScoped
    fun mainHeaderHandler(bind: MainHeaderHandlerImplDelegate): MainHeaderHandler
}