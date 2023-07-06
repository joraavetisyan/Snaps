package io.snaps.coredata.network

import android.app.Application
import android.content.Context
import android.telephony.TelephonyManager
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.snaps.coredata.database.TokenStorage
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.network.interceptors.AuthenticationInterceptor
import io.snaps.coredata.network.interceptors.CommonHeaderInterceptor
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
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
    fun chuckInterceptor(application: Application, collector: ChuckerCollector) =
        ChuckerInterceptor.Builder(application).collector(collector).build()

    @Provides
    @Singleton
    fun chuckCollector(application: Application) = ChuckerCollector(application)

    @Provides
    @Singleton
    fun provideTelephonyManager(@ApplicationContext context: Context): TelephonyManager {
        return context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }
}