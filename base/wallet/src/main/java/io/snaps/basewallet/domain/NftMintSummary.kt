package io.snaps.basewallet.domain

import io.snaps.corecommon.model.CryptoAddress
import java.math.BigDecimal

data class NftMintSummary(
    val from: CryptoAddress,
    val to: CryptoAddress,
    val summary: BigDecimal,
    val gas: BigDecimal,
    val total: BigDecimal,

    val gasLimit: Long,
    val transactionData: Any, // TransactionData
)