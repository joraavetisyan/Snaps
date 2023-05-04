package io.snaps.basewallet.domain

import io.snaps.corecommon.model.WalletAddress
import java.math.BigDecimal

data class NftMintSummary(
    val from: WalletAddress,
    val to: WalletAddress,
    val summary: BigDecimal,
    val gas: BigDecimal,
    val total: BigDecimal,

    val gasLimit: Long,
    val transactionData: Any, // TransactionData
)