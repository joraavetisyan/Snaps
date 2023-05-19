package io.snaps.featurecollection.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.snaps.featurecollection.domain.MyCollectionInteractor
import io.snaps.featurecollection.domain.MyCollectionInteractorImpl

@Module
@InstallIn(ViewModelComponent::class)
interface DataBindModule {

    @Binds
    @ViewModelScoped
    fun myCollectionInteractor(bind: MyCollectionInteractorImpl): MyCollectionInteractor
}