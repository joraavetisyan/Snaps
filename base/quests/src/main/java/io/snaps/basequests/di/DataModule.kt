package io.snaps.basequests.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basequests.data.QuestsApi
import io.snaps.basequests.data.QuestsRepository
import io.snaps.basequests.data.QuestsRepositoryImpl
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.di.UserSessionComponent
import io.snaps.coredata.di.UserSessionComponentManager
import io.snaps.coredata.di.UserSessionScope
import io.snaps.coredata.network.ApiConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun questsApi(config: ApiConfig): QuestsApi = config
            .serviceBuilder(QuestsApi::class.java)
            .interceptor(config.commonHeaderInterceptor)
            .interceptor(config.authenticationInterceptor)
            .build()
}

@Module
@InstallIn(UserSessionComponent::class)
interface DataBindModule {

    @Binds
    @UserSessionScope
    fun questsRepository(bind: QuestsRepositoryImpl): QuestsRepository
}

@EntryPoint
@InstallIn(UserSessionComponent::class)
internal interface DataBindEntryPoint {

    fun questsRepository(): QuestsRepository
}

@Module
@InstallIn(SingletonComponent::class)
internal object DataBindEntryPointBridge {

    @Bridged
    @Provides
    fun questsRepository(
        componentManager: UserSessionComponentManager,
    ): QuestsRepository {
        return EntryPoints
            .get(componentManager, DataBindEntryPoint::class.java)
            .questsRepository()
    }
}