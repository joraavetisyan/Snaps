package com.defince.basesession.data

import com.defince.basesources.NotificationsSource
import com.defince.corecommon.ext.log
import com.defince.corecommon.model.AppError
import com.defince.corecommon.model.Effect
import com.defince.coredata.network.Action
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

class ActionImpl @Inject constructor(
    private val notificationsSource: NotificationsSource,
    private val sessionRepository: SessionRepository,
) : Action {

    override suspend fun <T : Any> execute(
        needProcessErrors: Boolean,
        block: suspend CoroutineScope.() -> Effect<T>,
    ): Effect<T> {
        val effect = try {
            coroutineScope {
                var effect = block()
                if (needProcessErrors && effect.errorOrNull?.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
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
            if (needProcessErrors) it.errorOrNull?.let { error -> handle(error) }
        }
    }

    // тут обрабатывем общие ошибки (простые с плашкой),
    // остальные случаи обрабатываются на уровне view model
    private suspend fun handle(error: AppError) {
        log((error.cause ?: error).fillInStackTrace().stackTraceToString())

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