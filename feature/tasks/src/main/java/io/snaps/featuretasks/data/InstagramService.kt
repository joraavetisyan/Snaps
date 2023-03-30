package io.snaps.featuretasks.data

import android.net.Uri
import com.google.gson.Gson
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.featuretasks.data.model.instagram.AccessTokenResponseDto
import io.snaps.featuretasks.data.model.instagram.UserResponseDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.HttpURLConnection
import javax.inject.Inject

private const val BASE_AUTH_URL = "https://api.instagram.com/oauth/"
private const val BASE_URL = "https://graph.instagram.com/"
private const val INSTAGRAM_APP_ID = "1259862838218582"
private const val INSTAGRAM_APP_SECRET = "6bab8da282dcfdb28ad20446c9ed8c05"
private const val REDIRECT_URL = "https://snapsapp.io/"

class InstagramService @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    fun getAuthRequestUrl(): String {
        return "${BASE_AUTH_URL}authorize?client_id=$INSTAGRAM_APP_ID&redirect_uri=$REDIRECT_URL&scope=user_profile&response_type=code"
    }

    fun getRedirectUrl() = REDIRECT_URL

    fun getAuthCode(url: String): String? = Uri.parse(url).getQueryParameter("code")

    // todo build http request and response move to core-data
    suspend fun getUserInfo(accessToken: String): Effect<UserResponseDto> = withContext(ioDispatcher) {
        val url = "${BASE_URL}me?fields=id,username&access_token=$accessToken"
        val okHttpClient = OkHttpClient
            .Builder()
            .build()
        val request = Request
            .Builder()
            .url(url)
            .build()
        val response = okHttpClient.newCall(request).execute()
        if (response.code == HttpURLConnection.HTTP_OK) {
            val user = Gson().fromJson(response.body.string(), UserResponseDto::class.java) // todo kotlin serialization
            Effect.success(user)
        } else {
            Effect.error(AppError.Unknown())
        }
    }

    suspend fun getAccessToken(code: String): Effect<String> = withContext(ioDispatcher) {
        val url = "${BASE_AUTH_URL}access_token"
        val okHttpClient = OkHttpClient
            .Builder()
            .build()
        val requestBody = FormBody
            .Builder()
            .addEncoded("client_id", INSTAGRAM_APP_ID)
            .addEncoded("client_secret", INSTAGRAM_APP_SECRET)
            .addEncoded("grant_type", "authorization_code")
            .addEncoded("redirect_uri", REDIRECT_URL)
            .addEncoded("code", code)
            .build()
        val request = Request
            .Builder()
            .url(url)
            .post(requestBody)
            .build()
        val response = okHttpClient.newCall(request).execute()
        val body = response.body
        if (response.code == HttpURLConnection.HTTP_OK) {
            val accessToken = Gson().fromJson(body.string(), AccessTokenResponseDto::class.java) // todo kotlin serialization
            Effect.success(accessToken.accessToken)
        } else {
            Effect.error(AppError.Unknown())
        }
    }
}