package io.snaps.corecrypto.entities.transactionrecords.evm

import io.horizontalsystems.ethereumkit.models.Transaction
import io.horizontalsystems.marketkit.models.Token
import io.snaps.corecrypto.entities.TransactionValue
import io.snaps.corecrypto.other.TransactionSource

class EvmIncomingTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val from: String,
    val value: TransactionValue
) : EvmTransactionRecord(transaction, baseToken, source, true) {

    override val mainValue = value

}
