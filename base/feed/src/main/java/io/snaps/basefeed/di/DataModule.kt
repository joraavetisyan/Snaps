package io.snaps.basefeed.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import io.snaps.basefeed.data.CommentApi
import io.snaps.basefeed.data.CommentRepository
import io.snaps.basefeed.data.CommentRepositoryImpl
import io.snaps.basefeed.data.FakeCommentApi
import io.snaps.basefeed.data.FakeVideoFeedApi
import io.snaps.basefeed.data.UploadStatusSource
import io.snaps.basefeed.data.UploadStatusSourceApivideoWorkManagerImpl
import io.snaps.basefeed.data.VideoFeedUploader
import io.snaps.basefeed.data.VideoFeedUploaderApivideoWorkManagerImpl
import io.snaps.basefeed.data.VideoFeedApi
import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.basefeed.data.VideoFeedRepositoryImpl
import io.snaps.basefeed.ui.CreateCheckHandler
import io.snaps.basefeed.ui.CreateCheckHandlerImplDelegate
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.di.UserSessionComponent
import io.snaps.coredata.di.UserSessionComponentManager
import io.snaps.coredata.di.UserSessionScope
import io.snaps.coredata.network.ApiConfig
import io.snaps.coredata.network.ApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DataModule {

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
internal interface SingletonDataBindModule {

    @Binds
    @Singleton
    fun VideFeedUploader(bind: VideoFeedUploaderApivideoWorkManagerImpl): VideoFeedUploader

    @Binds
    @Singleton
    fun UploadStatusSource(source: UploadStatusSourceApivideoWorkManagerImpl): UploadStatusSource
}

@Module
@InstallIn(UserSessionComponent::class)
internal interface DataBindModule {

    @Binds
    @UserSessionScope
    fun videoFeedRepository(bind: VideoFeedRepositoryImpl): VideoFeedRepository

    @Binds
    @UserSessionScope
    fun commentRepository(bind: CommentRepositoryImpl): CommentRepository
}

@EntryPoint
@InstallIn(UserSessionComponent::class)
internal interface DataBindEntryPoint {

    fun videoFeedRepository(): VideoFeedRepository

    fun commentRepository(): CommentRepository
}

@Module
@InstallIn(SingletonComponent::class)
internal object DataBindEntryPointBridge {

    @Bridged
    @Provides
    fun videoFeedRepository(
        componentManager: UserSessionComponentManager,
    ): VideoFeedRepository {
        return EntryPoints
            .get(componentManager, DataBindEntryPoint::class.java)
            .videoFeedRepository()
    }

    @Bridged
    @Provides
    fun commentRepository(
        componentManager: UserSessionComponentManager,
    ): CommentRepository {
        return EntryPoints
            .get(componentManager, DataBindEntryPoint::class.java)
            .commentRepository()
    }
}

@Module
@InstallIn(ViewModelComponent::class)
internal interface ViewModelDataBindModule {

    @Binds
    @ViewModelScoped
    fun CreateCheckHandler(bind: CreateCheckHandlerImplDelegate): CreateCheckHandler
}