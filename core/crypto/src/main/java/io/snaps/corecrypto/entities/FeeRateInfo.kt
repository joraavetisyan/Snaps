package io.snaps.corecrypto.entities

import io.snaps.corecrypto.core.FeeRatePriority

data class FeeRateInfo(val priority: FeeRatePriority, var feeRate: Long, val duration: Long? = null)
