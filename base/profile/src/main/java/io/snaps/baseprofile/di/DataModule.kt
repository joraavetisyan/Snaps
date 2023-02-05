package io.snaps.baseprofile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.MainHeaderHandlerImplDelegate
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule

@Module
@InstallIn(SingletonComponent::class)
interface DataBindModule {

    @Binds
    @Singleton
    fun mainHeaderHandler(bind: MainHeaderHandlerImplDelegate): MainHeaderHandler
}