package io.snaps.basesubs.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.basesubs.data.FakeSubsApi
import io.snaps.basesubs.data.SubsApi
import io.snaps.basesubs.data.SubsRepository
import io.snaps.basesubs.data.SubsRepositoryImpl
import io.snaps.coredata.network.ApiConfig
import io.snaps.coredata.network.ApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun subsApi(config: ApiConfig, feature: FeatureToggle): SubsApi =
        if (feature.isEnabled(Feature.SubsApiMock)) FakeSubsApi()
        else config
            .serviceBuilder(SubsApi::class.java)
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
    fun subsRepository(bind: SubsRepositoryImpl): SubsRepository
}