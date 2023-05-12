package io.snaps.coredata.network

import io.snaps.corecommon.model.Effect
import kotlinx.coroutines.CoroutineScope

interface Action {

    suspend fun <T : Any> execute(
        needsErrorProcessing: Boolean = true,
        needsTokenExpireProcessing: Boolean = true,
        block: suspend CoroutineScope.() -> Effect<T>,
    ): Effect<T>
}