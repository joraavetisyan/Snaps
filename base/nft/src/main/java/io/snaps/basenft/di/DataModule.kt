package io.snaps.basenft.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basenft.data.FakeNftApi
import io.snaps.basenft.data.NftApi
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.data.NftRepositoryImpl
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
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
    fun nftApi(config: ApiConfig, feature: FeatureToggle): NftApi =
        if (feature.isEnabled(Feature.NftApiMock)) FakeNftApi()
        else config
            .serviceBuilder(NftApi::class.java)
            .interceptor(config.commonHeaderInterceptor)
            .interceptor(config.authenticationInterceptor)
            .build()
}

@Module
@InstallIn(UserSessionComponent::class)
interface DataBindModule {

    @Binds
    @UserSessionScope
    fun bind(bind: NftRepositoryImpl): NftRepository
}

@EntryPoint
@InstallIn(UserSessionComponent::class)
internal interface DataBindEntryPoint {

    fun nftRepository(): NftRepository
}

@Module
@InstallIn(SingletonComponent::class)
internal object DataBindEntryPointBridge {

    @Bridged
    @Provides
    fun nftRepository(
        componentManager: UserSessionComponentManager,
    ): NftRepository {
        return EntryPoints
            .get(componentManager, DataBindEntryPoint::class.java)
            .nftRepository()
    }
}