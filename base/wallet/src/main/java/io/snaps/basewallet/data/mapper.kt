package io.snaps.basewallet.data

import io.horizontalsystems.ethereumkit.api.jsonrpc.JsonRpc
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Effect
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.CancellationException

suspend inline fun <reified T : Any> blockchainCall(
    dispatcher: CoroutineDispatcher,
    noinline block: suspend () -> T,
): Effect<T> = withContext(dispatcher) {
    try {
        Effect.success(block())
    } catch (t: Throwable) {
        if (t is CancellationException) throw t

        val error = (t.cause as? JsonRpc.ResponseError.RpcError)?.error?.let {
            Exception(it.code.toString() + " " + it.message)
        } ?: (t.cause as? JsonRpc.ResponseError.InvalidResult)?.let {
            Exception(it.toString())
        } ?: t as? Exception
        Effect.error(AppError.Unknown(cause = error))
    }
}