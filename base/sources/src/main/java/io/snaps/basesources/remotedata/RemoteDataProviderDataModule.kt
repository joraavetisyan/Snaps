package io.snaps.basesources.remotedata

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteDataProviderDataModule {

    @Provides
    @Singleton
    fun RemoteDataProvider(feature: FeatureToggle): RemoteDataProvider =
        if (feature.isEnabled(Feature.RemoteDataProviderMock)) FakeRemoteDataProvider()
        else RemoteDataProviderFirebaseRemoteConfigImpl()
}