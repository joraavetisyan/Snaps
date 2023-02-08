package io.snaps.baseprofile.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.baseprofile.data.FakeProfileApi
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.MainHeaderHandlerImplDelegate
import io.snaps.baseprofile.data.ProfileApi
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.ProfileRepositoryImpl
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.coredata.network.ApiConfig
import io.snaps.coredata.network.ApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun profileApi(config: ApiConfig, feature: FeatureToggle): ProfileApi =
        if (feature.isEnabled(Feature.ProfileApiMock)) FakeProfileApi()
        else config
            .serviceBuilder(ProfileApi::class.java)
            .service(ApiService.General)
            .interceptor(config.commonHeaderInterceptor)
            .interceptor(config.authenticationInterceptor)
            .build()
}

@Module
@InstallIn(SingletonComponent::class)
interface DataBindModule {

    @Binds
    @Singleton
    fun mainHeaderHandler(bind: MainHeaderHandlerImplDelegate): MainHeaderHandler

    @Binds
    @Singleton
    fun profileRepository(repository: ProfileRepositoryImpl): ProfileRepository
}