package io.snaps.basesubs.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.basesubs.data.FakeSubsApi
import io.snaps.basesubs.data.SubsApi
import io.snaps.basesubs.data.SubsRepository
import io.snaps.basesubs.data.SubsRepositoryImpl
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.di.UserSessionComponent
import io.snaps.coredata.di.UserSessionComponentManager
import io.snaps.coredata.di.UserSessionScope
import io.snaps.coredata.network.ApiConfig
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
            .interceptor(config.commonHeaderInterceptor)
            .interceptor(config.authenticationInterceptor)
            .build()
}

@Module
@InstallIn(UserSessionComponent::class)
interface DataBindModule {

    @Binds
    @UserSessionScope
    fun subsRepository(bind: SubsRepositoryImpl): SubsRepository
}

@EntryPoint
@InstallIn(UserSessionComponent::class)
internal interface DataBindEntryPoint {

    fun subsRepository(): SubsRepository
}

@Module
@InstallIn(SingletonComponent::class)
internal object DataBindEntryPointBridge {

    @Bridged
    @Provides
    fun subsRepository(
        componentManager: UserSessionComponentManager,
    ): SubsRepository {
        return EntryPoints
            .get(componentManager, DataBindEntryPoint::class.java)
            .subsRepository()
    }
}