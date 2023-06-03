package io.snaps.featurewalletconnect.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.snaps.featurewalletconnect.presentation.viewmodel.WalletSecurityErrorHandler
import io.snaps.featurewalletconnect.presentation.viewmodel.WalletSecurityErrorHandlerImplDelegate

@Module
@InstallIn(ViewModelComponent::class)
interface DataBindModule {

    @Binds
    @ViewModelScoped
    fun WalletSecurityErrorHandler(bind: WalletSecurityErrorHandlerImplDelegate): WalletSecurityErrorHandler
}