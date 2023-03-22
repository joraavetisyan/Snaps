package io.snaps.featureinitialization.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.snaps.featureinitialization.domain.CreateUserInteractor
import io.snaps.featureinitialization.domain.CreateUserInteractorImpl

@Module
@InstallIn(ViewModelComponent::class)
interface DataBindModule {

    @Binds
    @ViewModelScoped
    fun createUserInteractor(bind: CreateUserInteractorImpl): CreateUserInteractor
}