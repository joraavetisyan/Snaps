package io.snaps.coredata.network.interceptors

import io.snaps.corecommon.model.generateUuid
import io.snaps.coredata.database.UserDataStorage
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.Header
import java.io.IOException

class CommonHeaderInterceptor(
    private val userDataStorage: UserDataStorage,
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val customRequest = chain.request().newBuilder()
            .method(originalRequest.method, originalRequest.body)
            .header("X-Request-ID", generateUuid())
            .header("Content-Type", "application/json;charset=utf-8")
            .header("Accept", "*/*")
            .run {
                userDataStorage.captchaResult?.let { header("g-recaptcha-response", it) } ?: this
            }
            .build()

        chain.request()

        return chain.proceed(customRequest)
    }
}