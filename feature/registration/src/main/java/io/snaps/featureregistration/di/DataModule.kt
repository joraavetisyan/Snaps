package io.snaps.featureregistration.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.coredata.network.ApiConfig
import io.snaps.featureregistration.data.AuthApi
import io.snaps.featureregistration.data.AuthRepository
import io.snaps.featureregistration.data.AuthRepositoryImpl
import io.snaps.featureregistration.data.FakeAuthApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun authApi(apiConfig: ApiConfig): AuthApi = FakeAuthApi()

    @Provides
    @Singleton
    fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
interface DataBindModule {

    @Binds
    @Singleton
    fun authRepository(repository: AuthRepositoryImpl): AuthRepository
}