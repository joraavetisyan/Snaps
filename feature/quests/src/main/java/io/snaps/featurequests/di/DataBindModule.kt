package io.snaps.featurequests.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.snaps.featurequests.domain.ConnectInstagramInteractor
import io.snaps.featurequests.domain.ConnectInstagramInteractorImpl

@Module
@InstallIn(ViewModelComponent::class)
interface DataBindModule {

    @Binds
    @ViewModelScoped
    fun connectInteractor(bind: ConnectInstagramInteractorImpl): ConnectInstagramInteractor
}