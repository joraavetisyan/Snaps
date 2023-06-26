package io.snaps.basewallet.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.basewallet.data.FakeWalletApi
import io.snaps.basewallet.data.WalletApi
import io.snaps.basewallet.data.WalletDataManager
import io.snaps.basewallet.data.WalletDataManagerImpl
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.data.WalletRepositoryImpl
import io.snaps.basewallet.data.blockchain.BlockchainTxRepository
import io.snaps.basewallet.data.blockchain.BlockchainTxRepositoryImpl
import io.snaps.basewallet.ui.LimitedGasDialogHandler
import io.snaps.basewallet.ui.LimitedGasDialogHandlerImplDelegate
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensDialogHandlerImplDelegate
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
    fun walletApi(config: ApiConfig, feature: FeatureToggle): WalletApi =
        if (feature.isEnabled(Feature.WalletApiMock)) FakeWalletApi()
        else config
            .serviceBuilder(WalletApi::class.java)
            .interceptor(config.commonHeaderInterceptor)
            .interceptor(config.authenticationInterceptor)
            .build()
}

@Module
@InstallIn(SingletonComponent::class)
interface DataBindSingletonModule {

    @Binds
    @Singleton
    fun WalletDataManager(bind: WalletDataManagerImpl): WalletDataManager
}

@Module
@InstallIn(UserSessionComponent::class)
interface DataBindModule {

    @Binds
    @UserSessionScope
    fun walletRepository(bind: WalletRepositoryImpl): WalletRepository

    @Binds
    @UserSessionScope
    fun blockchainTxRepository(bind: BlockchainTxRepositoryImpl): BlockchainTxRepository
}

@EntryPoint
@InstallIn(UserSessionComponent::class)
internal interface DataBindEntryPoint {

    fun walletRepository(): WalletRepository

    fun blockchainTxRepository(): BlockchainTxRepository
}

@Module
@InstallIn(SingletonComponent::class)
internal object DataBindEntryPointBridge {

    @Bridged
    @Provides
    fun walletRepository(
        componentManager: UserSessionComponentManager,
    ): WalletRepository {
        return EntryPoints
            .get(componentManager, DataBindEntryPoint::class.java)
            .walletRepository()
    }

    @Bridged
    @Provides
    fun blockchainTxRepository(
        componentManager: UserSessionComponentManager,
    ): BlockchainTxRepository {
        return EntryPoints
            .get(componentManager, DataBindEntryPoint::class.java)
            .blockchainTxRepository()
    }
}

@Module
@InstallIn(ViewModelComponent::class)
interface ViewModelDataBindModule {

    @Binds
    @ViewModelScoped
    fun transferTokensHandler(bind: TransferTokensDialogHandlerImplDelegate): TransferTokensDialogHandler

    @Binds
    @ViewModelScoped
    fun limitedGasDialogHandler(bind: LimitedGasDialogHandlerImplDelegate): LimitedGasDialogHandler
}