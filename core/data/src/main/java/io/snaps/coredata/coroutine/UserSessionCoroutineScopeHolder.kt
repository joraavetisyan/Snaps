package io.snaps.coredata.coroutine

import kotlinx.coroutines.CoroutineScope

interface UserSessionCoroutineScopeHolder {

    val userSessionScope: CoroutineScope
}