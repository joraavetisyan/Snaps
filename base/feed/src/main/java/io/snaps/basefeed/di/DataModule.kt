package io.snaps.basefeed.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
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

    @Provides
    @ViewModelScoped
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
@InstallIn(ViewModelComponent::class)
interface DataBindModule {

    @Binds
    @ViewModelScoped
    fun videoFeedRepository(bind: VideoFeedRepositoryImpl): VideoFeedRepository

    @Binds
    @ViewModelScoped
    fun commentRepository(bind: CommentRepositoryImpl): CommentRepository
}