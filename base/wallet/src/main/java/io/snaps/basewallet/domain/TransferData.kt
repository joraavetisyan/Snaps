package io.snaps.basewallet.domain

import io.snaps.corecommon.model.CryptoAddress

data class TransferData(
    val from: CryptoAddress,
    val to: CryptoAddress,
    val summary: Double,
    val gas: Double,
    val total: Double,
)