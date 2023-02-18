package io.snaps.corecrypto.entities.transactionrecords.bitcoin

import io.snaps.corecrypto.entities.TransactionValue
import io.snaps.corecrypto.other.TransactionLockInfo
import io.snaps.corecrypto.other.TransactionSource
import io.horizontalsystems.marketkit.models.Token
import java.math.BigDecimal

class BitcoinIncomingTransactionRecord(
    token: Token,
    uid: String,
    transactionHash: String,
    transactionIndex: Int,
    blockHeight: Int?,
    confirmationsThreshold: Int?,
    timestamp: Long,
    fee: BigDecimal?,
    failed: Boolean,
    lockInfo: TransactionLockInfo?,
    conflictingHash: String?,
    showRawTransaction: Boolean,
    amount: BigDecimal,
    val from: String?,
    memo: String? = null,
    source: TransactionSource
) : BitcoinTransactionRecord(
    uid = uid,
    transactionHash = transactionHash,
    transactionIndex = transactionIndex,
    blockHeight = blockHeight,
    confirmationsThreshold = confirmationsThreshold,
    timestamp = timestamp,
    fee = fee?.let { TransactionValue.CoinValue(token, it) },
    failed = failed,
    lockInfo = lockInfo,
    conflictingHash = conflictingHash,
    showRawTransaction = showRawTransaction,
    memo = memo,
    source = source
) {

    val value: TransactionValue = TransactionValue.CoinValue(token, amount)

    override val mainValue = value

}
