package io.snaps.featurecollection.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.coredata.network.ApiConfig
import io.snaps.coredata.network.ApiService
import io.snaps.featurecollection.data.FakeMyCollectionApi
import io.snaps.featurecollection.data.MyCollectionApi
import io.snaps.featurecollection.data.MyCollectionRepository
import io.snaps.featurecollection.data.MyCollectionRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
class DataModule {

    @Provides
    @ViewModelScoped
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
@InstallIn(ViewModelComponent::class)
interface DataBindModule {

    @Binds
    @ViewModelScoped
    fun myCollectionRepository(bind: MyCollectionRepositoryImpl): MyCollectionRepository
}