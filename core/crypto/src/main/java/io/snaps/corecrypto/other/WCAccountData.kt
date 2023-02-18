package io.snaps.corecrypto.other

import io.horizontalsystems.ethereumkit.models.Chain

data class WCAccountData(
    val eip: String,
    val chain: Chain,
    val address: String?
)