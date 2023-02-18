package io.snaps.corecrypto.entities.transactionrecords.binancechain

import io.snaps.corecrypto.core.adapters.BinanceAdapter
import io.snaps.corecrypto.entities.TransactionValue
import io.snaps.corecrypto.entities.transactionrecords.TransactionRecord
import io.snaps.corecrypto.other.TransactionSource
import io.horizontalsystems.binancechainkit.models.TransactionInfo
import io.horizontalsystems.marketkit.models.Token

abstract class BinanceChainTransactionRecord(
    transaction: TransactionInfo,
    feeToken: Token,
    source: TransactionSource
) : TransactionRecord(
    uid = transaction.hash,
    transactionHash = transaction.hash,
    transactionIndex = 0,
    blockHeight = transaction.blockNumber,
    confirmationsThreshold = BinanceAdapter.confirmationsThreshold,
    timestamp = transaction.date.time / 1000,
    failed = false,
    source = source
) {

    val fee = TransactionValue.CoinValue(feeToken, BinanceAdapter.transferFee)
    val memo = transaction.memo

}
