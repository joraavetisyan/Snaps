package io.snaps.coredata.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren

interface ApplicationCoroutineScopeHolder {

    val applicationScope: CoroutineScope
}

internal object UserSessionCoroutineScopeHolder {

    val userSessionScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun cancel() {
        userSessionScope.coroutineContext.cancelChildren()
    }
}