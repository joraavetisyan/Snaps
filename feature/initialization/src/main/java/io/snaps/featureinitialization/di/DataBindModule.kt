package io.snaps.featureinitialization.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.featureinitialization.data.WalletRepository
import io.snaps.featureinitialization.data.WalletRepositoryImpl
import io.snaps.featureinitialization.data.WordManager
import io.snaps.featureinitialization.data.WordManagerImpl
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