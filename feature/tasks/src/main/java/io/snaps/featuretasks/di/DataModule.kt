package io.snaps.featuretasks.di

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
import io.snaps.featuretasks.data.FakeTasksApi
import io.snaps.featuretasks.data.TasksApi
import io.snaps.featuretasks.data.TasksRepository
import io.snaps.featuretasks.data.TasksRepositoryImpl
import io.snaps.featuretasks.domain.ConnectInstagramInteractor
import io.snaps.featuretasks.domain.ConnectInstagramInteractorImpl

@Module
@InstallIn(ViewModelComponent::class)
class DataModule {

    @Provides
    @ViewModelScoped
    fun tasksApi(config: ApiConfig, feature: FeatureToggle): TasksApi =
        if (feature.isEnabled(Feature.TasksApiMock)) FakeTasksApi()
        else config
            .serviceBuilder(TasksApi::class.java)
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
    fun tasksRepository(bind: TasksRepositoryImpl): TasksRepository

    @Binds
    @ViewModelScoped
    fun connectInteractor(bind: ConnectInstagramInteractorImpl): ConnectInstagramInteractor
}