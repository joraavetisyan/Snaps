package io.snaps.basefeed.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basefeed.data.CommentApi
import io.snaps.basefeed.data.CommentRepository
import io.snaps.basefeed.data.CommentRepositoryImpl
import io.snaps.basefeed.data.FakeCommentApi
import io.snaps.basefeed.data.FakeVideoFeedApi
import io.snaps.basefeed.data.VideoFeedApi
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.data.VideoFeedRepositoryImpl
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
    fun videoFeedApi(config: ApiConfig, feature: FeatureToggle): VideoFeedApi =
        if (feature.isEnabled(Feature.FeedApiMock)) FakeVideoFeedApi()
        else config
            .serviceBuilder(VideoFeedApi::class.java)
            .service(ApiService.General)
            .interceptor(config.commonHeaderInterceptor)
            .interceptor(config.authenticationInterceptor)
            .build()

    @Provides
    @Singleton
    fun commentApi(config: ApiConfig, feature: FeatureToggle): CommentApi =
        if (feature.isEnabled(Feature.CommentApiMock)) FakeCommentApi()
        else config
            .serviceBuilder(CommentApi::class.java)
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
    fun videoFeedRepository(bind: VideoFeedRepositoryImpl): VideoFeedRepository

    @Binds
    @Singleton
    fun commentRepository(bind: CommentRepositoryImpl): CommentRepository
}