package io.snaps.corecrypto.entities.transactionrecords.binancechain

import io.snaps.corecrypto.entities.TransactionValue
import io.snaps.corecrypto.other.TransactionSource
import io.horizontalsystems.binancechainkit.models.TransactionInfo
import io.horizontalsystems.marketkit.models.Token

class BinanceChainIncomingTransactionRecord(
    transaction: TransactionInfo,
    feeToken: Token,
    token: Token,
    source: TransactionSource
) : BinanceChainTransactionRecord(transaction, feeToken, source) {
    val value = TransactionValue.CoinValue(token, transaction.amount.toBigDecimal())
    val from = transaction.from

    override val mainValue = value

}
