package io.snaps.baseauth.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.baseauth.data.AuthRepository
import io.snaps.baseauth.data.AuthRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

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