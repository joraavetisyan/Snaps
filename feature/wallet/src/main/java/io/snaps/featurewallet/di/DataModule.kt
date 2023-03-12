package io.snaps.featurewallet.di

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
import io.snaps.featurewallet.data.FakeTransactionsApi
import io.snaps.featurewallet.data.TransactionsApi
import io.snaps.featurewallet.data.TransactionsRepository
import io.snaps.featurewallet.data.TransactionsRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
class DataModule {

    @Provides
    @ViewModelScoped
    fun transactionsApi(config: ApiConfig, feature: FeatureToggle): TransactionsApi =
        if (feature.isEnabled(Feature.TransactionsApiMock)) FakeTransactionsApi()
        else config
            .serviceBuilder(TransactionsApi::class.java)
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
    fun transactionsRepository(bind: TransactionsRepositoryImpl): TransactionsRepository
}