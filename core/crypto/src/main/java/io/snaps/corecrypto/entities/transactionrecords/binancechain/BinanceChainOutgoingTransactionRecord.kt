package io.snaps.corecrypto.entities.transactionrecords.binancechain

import io.snaps.corecrypto.entities.TransactionValue
import io.snaps.corecrypto.other.TransactionSource
import io.horizontalsystems.binancechainkit.models.TransactionInfo
import io.horizontalsystems.marketkit.models.Token

class BinanceChainOutgoingTransactionRecord(
    transaction: TransactionInfo,
    feeToken: Token,
    token: Token,
    val sentToSelf: Boolean,
    source: TransactionSource
) : BinanceChainTransactionRecord(transaction, feeToken, source) {
    val value = TransactionValue.CoinValue(token, transaction.amount.toBigDecimal().negate())
    val to = transaction.to

    override val mainValue = value

}
