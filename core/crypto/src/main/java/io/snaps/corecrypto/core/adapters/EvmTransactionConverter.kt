package io.snaps.corecrypto.core.adapters

import io.horizontalsystems.erc20kit.decorations.ApproveEip20Decoration
import io.horizontalsystems.erc20kit.decorations.OutgoingEip20Decoration
import io.horizontalsystems.erc20kit.events.TokenInfo
import io.horizontalsystems.erc20kit.events.TransferEventInstance
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.horizontalsystems.ethereumkit.decorations.ContractCreationDecoration
import io.horizontalsystems.ethereumkit.decorations.IncomingDecoration
import io.horizontalsystems.ethereumkit.decorations.OutgoingDecoration
import io.horizontalsystems.ethereumkit.decorations.UnknownTransactionDecoration
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.FullTransaction
import io.horizontalsystems.ethereumkit.models.InternalTransaction
import io.horizontalsystems.ethereumkit.models.Transaction
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.marketkit.models.TokenQuery
import io.horizontalsystems.marketkit.models.TokenType
import io.snaps.corecrypto.core.ICoinManager
import io.snaps.corecrypto.core.managers.EvmKitWrapper
import io.snaps.corecrypto.core.managers.EvmLabelManager
import io.snaps.corecrypto.core.tokenIconPlaceholder
import io.snaps.corecrypto.entities.TransactionValue
import io.snaps.corecrypto.entities.transactionrecords.evm.ApproveTransactionRecord
import io.snaps.corecrypto.entities.transactionrecords.evm.ContractCallTransactionRecord
import io.snaps.corecrypto.entities.transactionrecords.evm.ContractCreationTransactionRecord
import io.snaps.corecrypto.entities.transactionrecords.evm.EvmIncomingTransactionRecord
import io.snaps.corecrypto.entities.transactionrecords.evm.EvmOutgoingTransactionRecord
import io.snaps.corecrypto.entities.transactionrecords.evm.EvmTransactionRecord
import io.snaps.corecrypto.entities.transactionrecords.evm.ExternalContractCallTransactionRecord
import io.snaps.corecrypto.other.TransactionSource
import java.math.BigDecimal
import java.math.BigInteger

class EvmTransactionConverter(
    private val coinManager: ICoinManager,
    private val evmKitWrapper: EvmKitWrapper,
    private val source: TransactionSource,
    private val baseToken: Token,
    private val evmLabelManager: EvmLabelManager
) {
    private val evmKit: EthereumKit
        get() = evmKitWrapper.evmKit

    fun transactionRecord(fullTransaction: FullTransaction): EvmTransactionRecord {
        val transaction = fullTransaction.transaction

        val transactionRecord = when (val decoration = fullTransaction.decoration) {
            is ContractCreationDecoration -> {
                ContractCreationTransactionRecord(transaction, baseToken, source)
            }

            is IncomingDecoration -> {
                EvmIncomingTransactionRecord(
                    transaction,
                    baseToken,
                    source,
                    decoration.from.eip55,
                    baseCoinValue(decoration.value, false)
                )
            }

            is OutgoingDecoration -> {
                EvmOutgoingTransactionRecord(
                    transaction,
                    baseToken,
                    source,
                    decoration.to.eip55,
                    baseCoinValue(decoration.value, true),
                    decoration.sentToSelf
                )
            }

            is OutgoingEip20Decoration -> {
                EvmOutgoingTransactionRecord(
                    transaction,
                    baseToken,
                    source,
                    decoration.to.eip55,
                    getEip20Value(
                        decoration.contractAddress,
                        decoration.value,
                        true,
                        decoration.tokenInfo
                    ),
                    decoration.sentToSelf
                )
            }

            is ApproveEip20Decoration -> {
                ApproveTransactionRecord(
                    transaction,
                    baseToken,
                    source,
                    decoration.spender.eip55,
                    getEip20Value(decoration.contractAddress, decoration.value, false)
                )
            }

            is UnknownTransactionDecoration -> {
                val address = evmKit.receiveAddress

                val internalTransactions =
                    decoration.internalTransactions.filter { it.to == address }

                val eip20Transfers =
                    decoration.eventInstances.mapNotNull { it as? TransferEventInstance }
                val incomingEip20Transfers =
                    eip20Transfers.filter { it.to == address && it.from != address }
                val outgoingEip20Transfers = eip20Transfers.filter { it.from == address }

                val contractAddress = transaction.to
                val value = transaction.value

                when {
                    transaction.from == address && contractAddress != null && value != null -> {
                        ContractCallTransactionRecord(
                            transaction, baseToken, source,
                            contractAddress.eip55,
                            transaction.input?.let { evmLabelManager.methodLabel(it) },
                            getInternalEvents(internalTransactions) +
                                    getIncomingEip20Events(incomingEip20Transfers),
                            getTransactionValueEvents(transaction) +
                                    getOutgoingEip20Events(outgoingEip20Transfers)
                        )
                    }
                    transaction.from != address && transaction.to != address -> {
                        ExternalContractCallTransactionRecord(
                            transaction, baseToken, source,
                            getInternalEvents(internalTransactions) +
                                    getIncomingEip20Events(incomingEip20Transfers),
                            getOutgoingEip20Events(outgoingEip20Transfers)
                        )
                    }
                    else -> null
                }
            }
            else -> null
        }

        return transactionRecord ?: EvmTransactionRecord(
            transaction = transaction,
            baseToken = baseToken,
            source = source,
            foreignTransaction = transaction.from != evmKit.receiveAddress
        )
    }

    private fun convertAmount(amount: BigInteger, decimal: Int, negative: Boolean): BigDecimal {
        var significandAmount = amount.toBigDecimal().movePointLeft(decimal).stripTrailingZeros()

        if (significandAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO
        }

        if (negative) {
            significandAmount = significandAmount.negate()
        }

        return significandAmount
    }

    private fun getEip20Value(
        tokenAddress: Address,
        amount: BigInteger,
        negative: Boolean,
        tokenInfo: TokenInfo? = null
    ): TransactionValue {
        val query = TokenQuery(evmKitWrapper.blockchainType, TokenType.Eip20(tokenAddress.hex))
        val token = coinManager.getToken(query)

        return when {
            token != null -> {
                TransactionValue.CoinValue(token, convertAmount(amount, token.decimals, negative))
            }
            tokenInfo != null -> {
                TransactionValue.TokenValue(
                    coinUid = "",
                    tokenName = tokenInfo.tokenName,
                    tokenCode = tokenInfo.tokenSymbol,
                    tokenDecimals = tokenInfo.tokenDecimal,
                    value = convertAmount(amount, tokenInfo.tokenDecimal, negative),
                    coinIconPlaceholder = evmKitWrapper.blockchainType.tokenIconPlaceholder
                )
            }
            else -> {
                TransactionValue.RawValue(value = amount)
            }
        }
    }

    private fun baseCoinValue(value: BigInteger, negative: Boolean): TransactionValue {
        val amount = convertAmount(value, baseToken.decimals, negative)

        return TransactionValue.CoinValue(baseToken, amount)
    }

    private fun getInternalEvents(internalTransactions: List<InternalTransaction>): List<EvmTransactionRecord.TransferEvent> {
        val events: MutableList<EvmTransactionRecord.TransferEvent> = mutableListOf()

        for (transaction in internalTransactions) {
            events.add(
                EvmTransactionRecord.TransferEvent(
                    transaction.from.eip55,
                    baseCoinValue(transaction.value, false)
                )
            )
        }

        return events
    }

    private fun getTransactionValueEvents(transaction: Transaction): List<EvmTransactionRecord.TransferEvent> {
        val value = transaction.value
        if (value == null || value <= BigInteger.ZERO) return listOf()

        return listOf(
            EvmTransactionRecord.TransferEvent(transaction.to?.eip55, baseCoinValue(value, true))
        )
    }

    private fun getIncomingEip20Events(incomingTransfers: List<TransferEventInstance>): List<EvmTransactionRecord.TransferEvent> {
        val events: MutableList<EvmTransactionRecord.TransferEvent> = mutableListOf()

        for (transfer in incomingTransfers) {
            events.add(
                EvmTransactionRecord.TransferEvent(
                    transfer.from.eip55,
                    getEip20Value(
                        transfer.contractAddress,
                        transfer.value,
                        false,
                        transfer.tokenInfo
                    )
                )
            )
        }

        return events
    }

    private fun getOutgoingEip20Events(outgoingTransfers: List<TransferEventInstance>): List<EvmTransactionRecord.TransferEvent> {
        val events: MutableList<EvmTransactionRecord.TransferEvent> = mutableListOf()

        for (transfer in outgoingTransfers) {
            events.add(
                EvmTransactionRecord.TransferEvent(
                    transfer.to.eip55,
                    getEip20Value(
                        transfer.contractAddress,
                        transfer.value,
                        true,
                        transfer.tokenInfo
                    )
                )
            )
        }

        return events
    }
}
