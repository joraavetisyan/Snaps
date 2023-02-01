package io.snaps.coredata.network

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.snaps.coredata.database.TokenStorage
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.network.interceptors.AuthenticationInterceptor
import io.snaps.coredata.network.interceptors.CommonHeaderInterceptor
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