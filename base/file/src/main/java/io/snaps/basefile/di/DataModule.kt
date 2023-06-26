package io.snaps.basefile.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basefile.data.FileApi
import io.snaps.basefile.data.FileRepository
import io.snaps.basefile.data.FileRepositoryImpl
import io.snaps.coredata.network.ApiConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun fileApi(config: ApiConfig) = config
        .serviceBuilder(FileApi::class.java)
        .interceptor(config.commonHeaderInterceptor)
        .interceptor(config.authenticationInterceptor)
        .build()
}

@Module
@InstallIn(SingletonComponent::class)
interface DataBindModule {

    @Binds
    @Singleton
    fun fileRepository(repository: FileRepositoryImpl): FileRepository
}