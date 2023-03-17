package io.snaps.corecrypto.entities.transactionrecords.solana

import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.solanakit.models.Transaction
import io.snaps.corecrypto.entities.TransactionValue
import io.snaps.corecrypto.other.TransactionSource

class SolanaIncomingTransactionRecord(
        transaction: Transaction,
        baseToken: Token,
        source: TransactionSource,
        val from: String?,
        val value: TransactionValue
): SolanaTransactionRecord(transaction, baseToken, source) {

    override val mainValue = value

}