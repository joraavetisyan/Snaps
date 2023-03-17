package io.snaps.corecrypto.entities.transactionrecords.evm

import io.horizontalsystems.ethereumkit.models.Transaction
import io.horizontalsystems.marketkit.models.Token
import io.snaps.corecrypto.entities.TransactionValue
import io.snaps.corecrypto.entities.transactionrecords.evm.EvmTransactionRecord.TransferEvent
import io.snaps.corecrypto.other.TransactionSource
import java.math.BigDecimal

class ExternalContractCallTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val incomingEvents: List<TransferEvent>,
    val outgoingEvents: List<TransferEvent>
) : EvmTransactionRecord(
    transaction = transaction,
    baseToken = baseToken,
    source = source,
    foreignTransaction = true,
    spam = isSpam(incomingEvents, outgoingEvents)
) {

    override val mainValue: TransactionValue?
        get() {
            val (incomingValues, outgoingValues) = combined(incomingEvents, outgoingEvents)

            return when {
                (incomingValues.isEmpty() && outgoingValues.size == 1) -> outgoingValues.first()
                (incomingValues.size == 1 && outgoingValues.isEmpty()) -> incomingValues.first()
                else -> null
            }
        }

}

private fun isSpam(
    incomingEvents: List<TransferEvent>,
    outgoingEvents: List<TransferEvent>
): Boolean {
    for (event in (incomingEvents + outgoingEvents)) {
        val value = event.value
        if (
            value is TransactionValue.CoinValue && value.value > BigDecimal.ZERO ||
            value is TransactionValue.NftValue && value.value > BigDecimal.ZERO
        ) {
            return false
        }
    }
    return true
}
