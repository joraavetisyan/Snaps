package io.snaps.corecrypto.entities.transactionrecords.solana

import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.solanakit.models.Transaction
import io.snaps.corecrypto.other.TransactionSource

class SolanaUnknownTransactionRecord(
        transaction: Transaction,
        baseToken: Token,
        source: TransactionSource,
        val incomingTransfers: List<Transfer>,
        val outgoingTransfers: List<Transfer>
): SolanaTransactionRecord(transaction, baseToken, source)
