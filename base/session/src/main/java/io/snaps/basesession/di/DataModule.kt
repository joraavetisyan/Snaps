package io.snaps.basesession.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basesession.data.ActionImpl
import io.snaps.basesession.data.AntiFraudHandler
import io.snaps.basesession.data.AntiFraudHandlerSafetyNetImpl
import io.snaps.basesession.data.LogoutApi
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesession.data.OnboardingHandlerImplDelegate
import io.snaps.basesession.data.SessionRepository
import io.snaps.basesession.data.SessionRepositoryImpl
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.di.UserSessionComponent
import io.snaps.coredata.di.UserSessionComponentManager
import io.snaps.coredata.di.UserSessionScope
import io.snaps.coredata.network.Action
import io.snaps.coredata.network.ApiConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun logoutApi(config: ApiConfig) = config.serviceBuilder(LogoutApi::class.java)
        .interceptor(config.commonHeaderInterceptor)
        .interceptor(config.authenticationInterceptor)
        .build()
}

@Module
@InstallIn(SingletonComponent::class)
interface DataBindSingletonModule {

    @Binds
    @Singleton
    fun action(action: ActionImpl): Action

    @Binds
    @Singleton
    fun antiFraud(bind: AntiFraudHandlerSafetyNetImpl): AntiFraudHandler

    @Binds
    @Singleton
    fun SessionRepository(bind: SessionRepositoryImpl): SessionRepository
}

@Module
@InstallIn(UserSessionComponent::class)
interface DataBindModule {

    @Binds
    @UserSessionScope
    fun OnboardingHandler(bind: OnboardingHandlerImplDelegate): OnboardingHandler
}

@EntryPoint
@InstallIn(UserSessionComponent::class)
internal interface DataBindEntryPoint {

    fun OnboardingHandler(): OnboardingHandler
}

@Module
@InstallIn(SingletonComponent::class)
internal object DataBindEntryPointBridge {

    @Bridged
    @Provides
    fun OnboardingHandler(
        componentManager: UserSessionComponentManager,
    ): OnboardingHandler {
        return EntryPoints
            .get(componentManager, DataBindEntryPoint::class.java)
            .OnboardingHandler()
    }
}