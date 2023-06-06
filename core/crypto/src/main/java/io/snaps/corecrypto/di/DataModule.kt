package io.snaps.corecrypto.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.corecrypto.core.CryptoKit
import io.snaps.corecrypto.core.IAccountFactory
import io.snaps.corecrypto.core.IAccountManager
import io.snaps.corecrypto.core.IAdapterManager
import io.snaps.corecrypto.core.IWalletManager
import io.snaps.corecrypto.core.IWordsManager
import io.snaps.corecrypto.core.managers.PassphraseValidator
import io.snaps.corecrypto.core.managers.WalletActivator
import io.snaps.corecrypto.core.providers.BalanceActiveWalletRepository
import io.snaps.corecrypto.core.providers.BalanceAdapterRepository
import io.snaps.corecrypto.core.providers.BalanceCache
import io.snaps.corecrypto.core.providers.BalanceService
import io.snaps.corecrypto.core.providers.BalanceViewItemFactory
import io.snaps.corecrypto.core.providers.BalanceXRateRepository
import io.snaps.corecrypto.core.providers.ITotalBalance
import io.snaps.corecrypto.core.providers.TotalBalance
import io.snaps.corecrypto.core.providers.TotalService

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    // todo don't use interfaces from crypto core directly, the crypto core module is likely to be removed
    @Provides
    fun accountFactory(): IAccountFactory = CryptoKit.accountFactory

    @Provides
    fun wordsManager(): IWordsManager = CryptoKit.wordsManager

    @Provides
    fun walletManager(): IWalletManager = CryptoKit.walletManager

    @Provides
    fun accountManager(): IAccountManager = CryptoKit.accountManager

    @Provides
    fun adapterManager(): IAdapterManager = CryptoKit.adapterManager

    @Provides
    fun walletActivator(): WalletActivator = CryptoKit.walletActivator

    @Provides
    fun passphraseValidator(): PassphraseValidator = PassphraseValidator()

    @Provides
    fun totalBalance(): ITotalBalance = TotalBalance(
        totalService = TotalService(
            currencyManager = CryptoKit.currencyManager,
            marketKit = CryptoKit.marketKit,
            baseTokenManager = CryptoKit.baseTokenManager,
            balanceHiddenManager = CryptoKit.balanceHiddenManager,
        ),
        balanceHiddenManager = CryptoKit.balanceHiddenManager,
    )

    @Provides
    fun balanceViewItemFactory(): BalanceViewItemFactory = BalanceViewItemFactory()

    @Provides
    fun balanceService(): BalanceService = BalanceService(
        activeWalletRepository = BalanceActiveWalletRepository(
            walletManager = CryptoKit.walletManager,
            evmSyncSourceManager = CryptoKit.evmSyncSourceManager
        ),
        xRateRepository = BalanceXRateRepository(
            currencyManager = CryptoKit.currencyManager,
            marketKit = CryptoKit.marketKit
        ),
        adapterRepository = BalanceAdapterRepository(
            adapterManager = CryptoKit.adapterManager,
            balanceCache = BalanceCache(dao = CryptoKit.appDatabase.enabledWalletsCacheDao())
        ),
        localStorage = CryptoKit.localStorage,
        connectivityManager = CryptoKit.connectivityManager,
        accountManager = CryptoKit.accountManager,
    )
}