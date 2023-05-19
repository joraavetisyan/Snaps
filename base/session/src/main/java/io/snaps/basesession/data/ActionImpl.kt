package io.snaps.basesession.data

import com.chuckerteam.chucker.api.ChuckerCollector
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
import javax.inject.Singleton

class ActionImpl @Inject constructor(
    private val buildInfo: BuildInfo,
    private val chuckerCollector: ChuckerCollector,
    private val notificationsSource: NotificationsSource,
    private val sessionRepository: SessionRepository,
) : Action {

    override suspend fun <T : Any> execute(
        needsErrorProcessing: Boolean,
        needsTokenExpireProcessing: Boolean,
        block: suspend CoroutineScope.() -> Effect<T>
    ): Effect<T> {
        val effect = try {
            coroutineScope {
                var effect = block()
                if (effect.errorOrNull?.code == HttpURLConnection.HTTP_UNAUTHORIZED && needsTokenExpireProcessing) {
                    sessionRepository.refresh()
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
                else -> {}
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface ActionModule {

    @Binds
    @Singleton
    fun action(action: ActionImpl): Action
}