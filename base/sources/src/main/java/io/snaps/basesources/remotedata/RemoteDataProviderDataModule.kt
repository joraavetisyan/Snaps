package io.snaps.basesources.remotedata

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.corecommon.model.BuildInfo
import io.snaps.coredata.network.ApiConfig
import io.snaps.coredata.network.ApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteDataProviderDataModule {

    @Provides
    @Singleton
    fun RemoteDataProvider(feature: FeatureToggle, buildInfo: BuildInfo): RemoteDataProvider =
        if (feature.isEnabled(Feature.RemoteDataProviderMock)) FakeRemoteDataProvider()
        else RemoteDataProviderFirebaseRemoteConfigImpl(buildInfo = buildInfo)


    @Provides
    @Singleton
    fun settingsApi(config: ApiConfig): SettingsApi = config
            .serviceBuilder(SettingsApi::class.java)
            .service(ApiService.General)
            .interceptor(config.commonHeaderInterceptor)
            .interceptor(config.authenticationInterceptor)
            .build()
}

@Module
@InstallIn(SingletonComponent::class)
interface RemoteDataProviderBindModule {

    @Binds
    @Singleton
    fun settingsRepository(bind: SettingsRepositoryImpl): SettingsRepository
}