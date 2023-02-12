package io.snaps.featureprofile.di

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
import io.snaps.featureprofile.data.FakeSubsApi
import io.snaps.featureprofile.data.SubsApi
import io.snaps.featureprofile.data.SubsRepository
import io.snaps.featureprofile.data.SubsRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
class DataModule {

    @Provides
    @ViewModelScoped
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
@InstallIn(ViewModelComponent::class)
interface DataBindModule {

    @Binds
    @ViewModelScoped
    fun subsRepository(bind: SubsRepositoryImpl): SubsRepository
}