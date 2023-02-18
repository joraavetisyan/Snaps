package io.snaps.corecrypto.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.corecrypto.core.CryptoKit
import io.snaps.corecrypto.core.IAccountFactory
import io.snaps.corecrypto.core.IAccountManager
import io.snaps.corecrypto.core.IWalletManager
import io.snaps.corecrypto.core.IWordsManager
import io.snaps.corecrypto.core.managers.PassphraseValidator
import io.snaps.corecrypto.core.managers.WalletActivator
import io.snaps.corecrypto.core.providers.PredefinedBlockchainSettingsProvider

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    fun accountFactory(): IAccountFactory = CryptoKit.accountFactory

    @Provides
    fun wordsManager(): IWordsManager = CryptoKit.wordsManager

    @Provides
    fun walletManager(): IWalletManager = CryptoKit.walletManager

    @Provides
    fun accountManager(): IAccountManager = CryptoKit.accountManager

    @Provides
    fun walletActivator(): WalletActivator = CryptoKit.walletActivator

    @Provides
    fun passphraseValidator(): PassphraseValidator = PassphraseValidator()

    @Provides
    fun predefinedBlockchainSettingsProvider(): PredefinedBlockchainSettingsProvider =
        PredefinedBlockchainSettingsProvider(
            manager = CryptoKit.restoreSettingsManager,
            zcashBirthdayProvider = CryptoKit.zcashBirthdayProvider,
        )
}