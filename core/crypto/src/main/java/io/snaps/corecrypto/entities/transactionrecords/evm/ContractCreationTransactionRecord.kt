package io.snaps.corecrypto.entities.transactionrecords.evm

import io.horizontalsystems.ethereumkit.models.Transaction
import io.horizontalsystems.marketkit.models.Token
import io.snaps.corecrypto.other.TransactionSource

class ContractCreationTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource
) : EvmTransactionRecord(transaction, baseToken, source)
