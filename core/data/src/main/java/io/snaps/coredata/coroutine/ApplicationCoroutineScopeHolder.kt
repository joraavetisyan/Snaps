package io.snaps.coredata.coroutine

import kotlinx.coroutines.CoroutineScope

interface ApplicationCoroutineScopeHolder {

    val applicationScope: CoroutineScope
}