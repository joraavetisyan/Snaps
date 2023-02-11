package io.snaps.basefeed.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.snaps.basefeed.data.FakeVideoFeedApi
import io.snaps.basefeed.data.VideoFeedApi
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.data.VideoFeedRepositoryImpl
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.coredata.network.ApiConfig
import io.snaps.coredata.network.ApiService

@Module
@InstallIn(ViewModelComponent::class)
class DataModule {

    @Provides
    @ViewModelScoped
    fun videoFeedApi(config: ApiConfig, feature: FeatureToggle): VideoFeedApi =
        if (feature.isEnabled(Feature.FeedApiMock)) FakeVideoFeedApi()
        else config
            .serviceBuilder(VideoFeedApi::class.java)
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
    fun videoFeedRepository(bind: VideoFeedRepositoryImpl): VideoFeedRepository
}