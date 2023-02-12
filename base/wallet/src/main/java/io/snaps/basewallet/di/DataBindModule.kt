package io.snaps.basewallet.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.data.WalletRepositoryImpl
import io.snaps.basewallet.data.WordManager
import io.snaps.basewallet.data.WordManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataBindModule {

    @Binds
    @Singleton
    fun walletRepository(bind: WalletRepositoryImpl): WalletRepository

    @Binds
    @Singleton
    fun wordManager(bind: WordManagerImpl): WordManager
}