package io.snaps.baseprofile.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.baseprofile.data.FakeProfileApi
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.MainHeaderHandlerImplDelegate
import io.snaps.baseprofile.data.ProfileApi
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.ProfileRepositoryImpl
import io.snaps.baseprofile.domain.EditUserInteractor
import io.snaps.baseprofile.domain.EditUserInteractorImpl
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.di.UserSessionComponent
import io.snaps.coredata.di.UserSessionComponentManager
import io.snaps.coredata.di.UserSessionScope
import io.snaps.coredata.network.ApiConfig
import io.snaps.coredata.network.ApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun profileApi(config: ApiConfig, feature: FeatureToggle): ProfileApi =
        if (feature.isEnabled(Feature.ProfileApiMock)) FakeProfileApi()
        else config
            .serviceBuilder(ProfileApi::class.java)
            .service(ApiService.General)
            .interceptor(config.commonHeaderInterceptor)
            .interceptor(config.authenticationInterceptor)
            .build()
}

@Module
@InstallIn(UserSessionComponent::class)
interface DataBindModule {

    @Binds
    @UserSessionScope
    fun mainHeaderHandler(bind: MainHeaderHandlerImplDelegate): MainHeaderHandler

    @Binds
    @UserSessionScope
    fun profileRepository(bind: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @UserSessionScope
    fun editUserInteractor(bind: EditUserInteractorImpl): EditUserInteractor
}

@EntryPoint
@InstallIn(UserSessionComponent::class)
internal interface DataBindEntryPoint {

    fun mainHeaderHandler(): MainHeaderHandler

    fun profileRepository(): ProfileRepository

    fun editUserInteractor(): EditUserInteractor
}

@Module
@InstallIn(SingletonComponent::class)
internal object DataBindEntryPointBridge {

    @Bridged
    @Provides
    fun mainHeaderHandler(
        componentManager: UserSessionComponentManager,
    ): MainHeaderHandler {
        return EntryPoints
            .get(componentManager, DataBindEntryPoint::class.java)
            .mainHeaderHandler()
    }

    @Bridged
    @Provides
    fun profileRepository(
        componentManager: UserSessionComponentManager,
    ): ProfileRepository {
        return EntryPoints
            .get(componentManager, DataBindEntryPoint::class.java)
            .profileRepository()
    }

    @Bridged
    @Provides
    fun editUserInteractor(
        componentManager: UserSessionComponentManager,
    ): EditUserInteractor {
        return EntryPoints
            .get(componentManager, DataBindEntryPoint::class.java)
            .editUserInteractor()
    }
}