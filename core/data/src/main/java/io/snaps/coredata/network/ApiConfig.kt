package io.snaps.coredata.network

import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.snaps.corecommon.model.BuildInfo
import io.snaps.coredata.network.interceptors.AuthenticationInterceptor
import io.snaps.coredata.network.interceptors.CommonHeaderInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ApiConfig @Inject constructor(
    val authenticationInterceptor: AuthenticationInterceptor,
    val commonHeaderInterceptor: CommonHeaderInterceptor,

    private val loggerInterceptor: HttpLoggingInterceptor,
    private val chuckerInterceptor: ChuckerInterceptor,

    private val converter: Converter.Factory,
    private val buildInfo: BuildInfo,
) {

    fun <API> serviceBuilder(clazz: Class<API>): ServiceParams<API> = ServiceParams(clazz)

    fun okHttpBuilder() = OkHttpParams()

    private fun createRetrofit(baseUrl: String, interceptors: Set<Interceptor>): Retrofit {
        return retrofitBuilder(baseUrl, createOkHttpClient(interceptors)).build()
    }

    private fun retrofitBuilder(baseUrl: String, okHttp: OkHttpClient) = Retrofit.Builder()
        .addConverterFactory(converter)
        .client(okHttp)
        .baseUrl(baseUrl)

    private fun createOkHttpClient(interceptors: Set<Interceptor>): OkHttpClient {
        val builder = okHttpClientBuilder()
        interceptors.forEach { builder.addInterceptor(it) }
        return builder.build()
    }

    private fun okHttpClientBuilder(): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_1_1))
            .followRedirects(true)

        if (buildInfo.isDebug) {
            builder.addInterceptor(chuckerInterceptor)
            builder.addInterceptor(loggerInterceptor)
        }
        return builder
    }

    inner class ServiceParams<API>(var clazz: Class<API>) {

        private var service: ApiService? = null
        private val interceptors = mutableSetOf<Interceptor>()

        fun service(service: ApiService) = this.apply { this.service = service }

        fun interceptor(interceptor: Interceptor) = this.apply { interceptors += interceptor }

        fun build(): API = createRetrofit(
            requireNotNull(service).getBaseUrl(buildInfo),
            interceptors
        ).create(clazz)
    }

    inner class OkHttpParams {

        private val interceptors = mutableSetOf<Interceptor>()

        fun interceptor(interceptor: Interceptor) = this.apply { interceptors += interceptor }

        fun build() = createOkHttpClient(interceptors)
    }
}