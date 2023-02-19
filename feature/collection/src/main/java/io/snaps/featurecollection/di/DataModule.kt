package io.snaps.featurecollection.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.coredata.network.ApiConfig
import io.snaps.coredata.network.ApiService
import io.snaps.featurecollection.data.FakeMyCollectionApi
import io.snaps.featurecollection.data.MyCollectionApi
import io.snaps.featurecollection.data.MyCollectionRepository
import io.snaps.featurecollection.data.MyCollectionRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun myCollectionApi(config: ApiConfig, feature: FeatureToggle): MyCollectionApi =
        if (feature.isEnabled(Feature.MyCollectionApiMock)) FakeMyCollectionApi()
        else config
            .serviceBuilder(MyCollectionApi::class.java)
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
    fun myCollectionRepository(bind: MyCollectionRepositoryImpl): MyCollectionRepository
}