package io.snaps.coredata.network

import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.cache.CacheProvider
import io.snaps.coredata.json.KotlinxSerializationJsonProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.CancellationException
import javax.net.ssl.SSLException
import kotlin.reflect.typeOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

suspend inline fun <reified T : Any> cachedApiCall(
    key: String,
    cacheProvider: CacheProvider,
    dispatcher: CoroutineDispatcher,
    needActualData: Boolean = false,
    lifeDuration: Duration = 5.minutes,
    noinline request: suspend () -> BaseResponse<T>,
): Effect<T> {
    return if (needActualData) {
        apiCall(dispatcher, request)
            .doOnSuccess { cacheProvider.save(typeOf<T>(), key, it, lifeDuration) }
            .applyCacheData { cacheProvider.get(typeOf<T>(), key) }
    } else {
        cacheProvider.get<T>(typeOf<T>(), key)
            ?.let { Effect.success(it) }
            ?: apiCall(dispatcher, request)
                .doOnSuccess { cacheProvider.save(typeOf<T>(), key, it, lifeDuration) }
    }
}

suspend inline fun <reified T : Any> apiCall(
    dispatcher: CoroutineDispatcher,
    noinline block: suspend () -> BaseResponse<T>,
): Effect<T> = withContext(dispatcher) {
    try {
        block().toEffect()
    } catch (t: Throwable) {
        Effect.error(t.toApiError())
    }
}

inline fun <reified T : Any> BaseResponse<T>.toEffect() = when {
    data != null -> Effect.success(data = data)
    else -> when (T::class) {
        Completable::class -> Effect.success(Completable as T)
        else -> Effect.error(error = AppError.Unknown())
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun Throwable.toApiError() = when (this) {
    is UnknownHostException,
    is SSLException,
    is ConnectException,
    is SocketTimeoutException,
    is SocketException -> AppError.Custom(cause = this as Exception)

    is CancellationException -> throw this

    is HttpException -> {
        val errorBody = try {
            KotlinxSerializationJsonProvider().get()
                .decodeFromStream<ErrorResponse>(response()!!.errorBody()!!.byteStream())
        } catch (t: Throwable) {
            log("Couldn't retrieve error body: $t")
            null
        }
        val error = errorBody?.error
        val errorCode = response()?.code()

        when {
            error == null && errorCode == null -> AppError.Unknown()

            else -> AppError.Custom(
                message = error?.message,
                displayMessage = error?.displayMessage,
                code = errorCode,
                cause = this as Exception,
            )
        }
    }

    else -> AppError.Unknown().also {
        log("Unknown api call error: $this")
    }
}