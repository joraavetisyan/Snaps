package io.snaps.featurecollection.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.featurecollection.domain.MyCollectionInteractor
import io.snaps.featurecollection.domain.MyCollectionInteractorImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataBindModule {

    @Binds
    @Singleton
    fun myCollectionInteractor(bind: MyCollectionInteractorImpl): MyCollectionInteractor
}