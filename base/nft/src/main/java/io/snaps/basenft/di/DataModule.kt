package io.snaps.basenft.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basenft.data.FakeNftApi
import io.snaps.basenft.data.NftApi
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.data.NftRepositoryImpl
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
    fun myCollectionApi(config: ApiConfig, feature: FeatureToggle): NftApi =
        if (feature.isEnabled(Feature.NftApiMock)) FakeNftApi()
        else config
            .serviceBuilder(NftApi::class.java)
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
    fun myCollectionRepository(bind: NftRepositoryImpl): NftRepository
}