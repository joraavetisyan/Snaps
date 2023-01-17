package com.defince.featuremain.di

import com.defince.coredata.network.ApiConfig
import com.defince.coredata.network.ApiService
import com.defince.featuremain.data.StubApi
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
        .service(ApiService.Stub)
        .interceptor(config.authentication)
        .build()
}