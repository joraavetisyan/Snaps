package io.snaps.featureprofile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.snaps.featureprofile.domain.EditProfileInteractor
import io.snaps.featureprofile.domain.EditProfileInteractorImpl

@Module
@InstallIn(ViewModelComponent::class)
interface DataBindModule {

    @Binds
    @ViewModelScoped
    fun editProfileInteractor(bind: EditProfileInteractorImpl): EditProfileInteractor
}