package io.snaps.featuremain.di

import io.snaps.coredata.network.ApiConfig
import io.snaps.coredata.network.ApiService
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.MainHeaderHandlerImplDelegate
import io.snaps.featuremain.data.StubApi
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