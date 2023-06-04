package io.snaps.basesession.data

import com.chuckerteam.chucker.api.ChuckerCollector
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.ext.logTag
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.BuildInfo
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.network.Action
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import java.net.HttpURLConnection
import javax.inject.Inject

class ActionImpl @Inject constructor(
    private val buildInfo: BuildInfo,
    private val chuckerCollector: ChuckerCollector,
    private val notificationsSource: NotificationsSource,
    private val sessionRepository: SessionRepository,
    private val antiFraudHandler: AntiFraudHandler,
) : Action {

    override suspend fun <T : Any> execute(
        needsErrorProcessing: Boolean,
        needsTokenExpireProcessing: Boolean,
        needsFraudProcessing: Boolean,
        block: suspend CoroutineScope.() -> Effect<T>
    ): Effect<T> {
        val effect = try {
            coroutineScope {
                var effect = block()
                val code = effect.errorOrNull?.code
                if (code == HttpURLConnection.HTTP_UNAUTHORIZED && needsTokenExpireProcessing) {
                    if (sessionRepository.tryRefresh().isSuccess) {
                        effect = block()
                    }
                } else if (code == 429 && needsFraudProcessing) {
                    antiFraudHandler.setCaptcha()
                    effect = block()
                }
                effect
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            } else {
                Effect.error(AppError.Unknown(cause = e))
            }
        }

        return effect.also {
            it.errorOrNull?.let { error ->
                if (buildInfo.isDebug) {
                    error.cause?.let { t -> chuckerCollector.onError(logTag, t) }
                }
                if (needsErrorProcessing) {
                    handle(error)
                }
            }
        }
    }

    // todo en
    // тут обрабатывем общие ошибки (простые с плашкой),
    // остальные случаи обрабатываются на уровне view model
    private suspend fun handle(error: AppError) {
        log(error)

        when (error) {
            is AppError.Unknown -> notificationsSource.sendError(error)
            is AppError.Custom -> when (error.code) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> {
                    sessionRepository.logout()
                    notificationsSource.sendError(error)
                }
                HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                    notificationsSource.sendError(error)
                }
                else -> {}
            }
        }
    }
}