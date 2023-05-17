package io.snaps.basewallet.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.basewallet.data.BlockchainTxRepository
import io.snaps.basewallet.data.BlockchainTxRepositoryImpl
import io.snaps.basewallet.data.FakeWalletApi
import io.snaps.basewallet.data.WalletApi
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.data.WalletRepositoryImpl
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensDialogHandlerImplDelegate
import io.snaps.coredata.network.ApiConfig
import io.snaps.coredata.network.ApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun walletApi(config: ApiConfig, feature: FeatureToggle): WalletApi =
        if (feature.isEnabled(Feature.WalletApiMock)) FakeWalletApi()
        else config
            .serviceBuilder(WalletApi::class.java)
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
    fun walletRepository(bind: WalletRepositoryImpl): WalletRepository

    @Binds
    @Singleton
    fun blockchainTxRepository(bind: BlockchainTxRepositoryImpl): BlockchainTxRepository
}

@Module
@InstallIn(ViewModelComponent::class)
interface ViewModelDataBindModule {

    @Binds
    @ViewModelScoped
    fun transferTokensHandler(bind: TransferTokensDialogHandlerImplDelegate): TransferTokensDialogHandler
}