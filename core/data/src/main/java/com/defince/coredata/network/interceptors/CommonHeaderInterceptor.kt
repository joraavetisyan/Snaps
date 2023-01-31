package com.defince.coredata.network.interceptors

import com.defince.corecommon.model.generateRequestId
import com.defince.coredata.database.UserDataStorage
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class CommonHeaderInterceptor(
    private val userDataStorage: UserDataStorage,
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val customRequest = chain.request().newBuilder()
            .method(originalRequest.method, originalRequest.body)
            .header("X-Request-ID", generateRequestId())
            .header("Content-Type", "application/json;charset=utf-8")
            .header("Accept", "*/*")
            .build()

        chain.request()

        return chain.proceed(customRequest)
    }
}