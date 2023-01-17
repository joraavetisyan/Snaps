package com.defince.coredata.coroutine

import kotlinx.coroutines.CoroutineScope

interface ApplicationCoroutineScopeHolder {

    val applicationScope: CoroutineScope
}