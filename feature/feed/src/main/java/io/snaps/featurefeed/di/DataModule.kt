package io.snaps.featurefeed.di

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
import io.snaps.featurefeed.data.CommentApi
import io.snaps.featurefeed.data.CommentRepository
import io.snaps.featurefeed.data.CommentRepositoryImpl
import io.snaps.featurefeed.data.FakeCommentApi

@Module
@InstallIn(ViewModelComponent::class)
class DataModule {

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
    fun commentRepository(bind: CommentRepositoryImpl): CommentRepository
}