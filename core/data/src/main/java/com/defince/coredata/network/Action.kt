package com.defince.coredata.network

import com.defince.corecommon.model.Effect
import kotlinx.coroutines.CoroutineScope

interface Action {

    suspend fun <T : Any> execute(
        needProcessErrors: Boolean,
        block: suspend CoroutineScope.() -> Effect<T>,
    ): Effect<T>
}