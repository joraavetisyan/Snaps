package io.snaps.corecrypto.entities.transactionrecords.evm

import io.snaps.corecrypto.entities.TransactionValue
import io.snaps.corecrypto.other.TransactionSource
import io.horizontalsystems.ethereumkit.models.Transaction
import io.horizontalsystems.marketkit.models.Token

class UnknownSwapTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val exchangeAddress: String,
    val valueIn: TransactionValue?,
    val valueOut: TransactionValue?,
) : EvmTransactionRecord(transaction, baseToken, source)
