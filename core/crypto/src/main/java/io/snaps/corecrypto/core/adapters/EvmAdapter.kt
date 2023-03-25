package io.snaps.corecrypto.core.adapters

import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.Chain
import io.horizontalsystems.ethereumkit.models.TransactionData
import io.reactivex.Flowable
import io.snaps.corecrypto.core.AdapterState
import io.snaps.corecrypto.core.BalanceData
import io.snaps.corecrypto.core.CryptoKit
import io.snaps.corecrypto.core.ICoinManager
import io.snaps.corecrypto.core.managers.EvmKitWrapper
import java.math.BigInteger

class EvmAdapter(evmKitWrapper: EvmKitWrapper, coinManager: ICoinManager) :
    BaseEvmAdapter(evmKitWrapper, decimal, coinManager) {

    // IAdapter

    override fun start() {
        // started via EthereumKitManager
    }

    override fun stop() {
        // stopped via EthereumKitManager
    }

    override fun refresh() {
        // refreshed via EthereumKitManager
    }

    // IBalanceAdapter

    override val balanceState: AdapterState
        get() = convertToAdapterState(evmKit.syncState)

    override val balanceStateUpdatedFlowable: Flowable<Unit>
        get() = evmKit.syncStateFlowable.map {}

    override val balanceData: BalanceData
        get() = BalanceData(balanceInBigDecimal(evmKit.accountState?.balance, decimal))

    override val balanceUpdatedFlowable: Flowable<Unit>
        get() = evmKit.accountStateFlowable.map { }

    private fun convertToAdapterState(syncState: EthereumKit.SyncState): AdapterState =
        when (syncState) {
            is EthereumKit.SyncState.Synced -> AdapterState.Synced
            is EthereumKit.SyncState.NotSynced -> AdapterState.NotSynced(syncState.error)
            is EthereumKit.SyncState.Syncing -> AdapterState.Syncing()
        }

    // ISendEthereumAdapter

    override fun getTransactionData(
        amount: BigInteger,
        address: Address,
        data: ByteArray,
    ): TransactionData {
        return TransactionData(to = address, value = amount, input = data)
    }

    companion object {
        const val decimal = 18

        fun clear(walletId: String, testMode: Boolean) {
            val networkTypes = when {
                testMode -> listOf(Chain.EthereumGoerli)
                else -> listOf(
                    Chain.Ethereum,
                    Chain.BinanceSmartChain,
                    Chain.Polygon,
                    Chain.Avalanche,
                    Chain.Optimism,
                    Chain.ArbitrumOne,
                    Chain.Gnosis,
                )
            }
            networkTypes.forEach {
                EthereumKit.clear(CryptoKit.instance, it, walletId)
            }
        }
    }

}
