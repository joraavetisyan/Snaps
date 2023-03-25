package io.snaps.featurewallet.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.snaps.featurewallet.data.TransactionsRepository
import io.snaps.featurewallet.data.TransactionsRepositoryImpl
import io.snaps.featurewallet.domain.WalletInteractor
import io.snaps.featurewallet.domain.WalletInteractorImpl
import io.snaps.featurewallet.viewmodel.CryptoSendHandler
import io.snaps.featurewallet.viewmodel.CryptoSendHandlerImplDelegate

@Module
@InstallIn(ViewModelComponent::class)
interface DataBindModule {

    @Binds
    @ViewModelScoped
    fun transactionsRepository(bind: TransactionsRepositoryImpl): TransactionsRepository

    @Binds
    @ViewModelScoped
    fun walletInteractor(bind: WalletInteractorImpl): WalletInteractor

    @Binds
    @ViewModelScoped
    fun cryptoSendHandler(bind: CryptoSendHandlerImplDelegate): CryptoSendHandler
}