package io.snaps.coredata.network.interceptors

import io.snaps.coredata.database.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

private const val AUTHORIZATION_HEADER = "Authorization"

class AuthenticationInterceptor(
    private val tokenStorage: TokenStorage,
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val customRequest = chain.request().newBuilder()
            .method(originalRequest.method, originalRequest.body)
            .header(AUTHORIZATION_HEADER, "${tokenStorage.authToken}")
            .build()

        return chain.proceed(customRequest)
    }
}