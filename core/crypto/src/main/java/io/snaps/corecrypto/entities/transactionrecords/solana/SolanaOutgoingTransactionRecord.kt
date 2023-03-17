package io.snaps.corecrypto.entities.transactionrecords.solana

import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.solanakit.models.Transaction
import io.snaps.corecrypto.entities.TransactionValue
import io.snaps.corecrypto.other.TransactionSource

class SolanaOutgoingTransactionRecord(
        transaction: Transaction,
        baseToken: Token,
        source: TransactionSource,
        val to: String?,
        val value: TransactionValue,
        val sentToSelf: Boolean
): SolanaTransactionRecord(transaction, baseToken, source) {

    override val mainValue = value

}
