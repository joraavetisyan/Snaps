package com.defince.coredata.network

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.defince.coredata.database.TokenStorage
import com.defince.coredata.database.UserDataStorage
import com.defince.coredata.network.interceptors.AuthenticationInterceptor
import com.defince.coredata.network.interceptors.CommonHeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    @Provides
    @Singleton
    fun authenticationInterceptor(tokenStorage: TokenStorage) =
        AuthenticationInterceptor(tokenStorage)

    @Provides
    @Singleton
    fun commonHeaderInterceptor(storage: UserDataStorage) = CommonHeaderInterceptor(storage)

    @Provides
    @Singleton
    fun chuckInterceptor(application: Application) = ChuckerInterceptor.Builder(application).build()
}