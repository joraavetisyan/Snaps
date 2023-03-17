package io.snaps.corecrypto.core.managers

import io.horizontalsystems.marketkit.models.BlockchainType
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.snaps.corecrypto.core.IAdapter
import io.snaps.corecrypto.core.IAdapterManager
import io.snaps.corecrypto.core.ITransactionsAdapter
import io.snaps.corecrypto.core.factories.AdapterFactory
import io.snaps.corecrypto.core.subscribeIO
import io.snaps.corecrypto.entities.Wallet
import io.snaps.corecrypto.other.TransactionSource
import java.util.concurrent.ConcurrentHashMap

class TransactionAdapterManager(
    private val adapterManager: IAdapterManager,
    private val adapterFactory: AdapterFactory
) {
    private val disposables = CompositeDisposable()

    private val adaptersReadySubject = BehaviorSubject.create<Unit>()
    val adaptersReadyObservable: Observable<Unit> get() = adaptersReadySubject

    private val adaptersMap = ConcurrentHashMap<TransactionSource, ITransactionsAdapter>()

    init {
        adapterManager.adaptersReadyObservable
            .subscribeIO {
                initAdapters(it)
            }
            .let {
                disposables.add(it)
            }
    }

    fun getAdapter(source: TransactionSource): ITransactionsAdapter? = adaptersMap[source]

    private fun initAdapters(adaptersMap: Map<Wallet, IAdapter>) {
        val currentAdapters = this.adaptersMap.toMutableMap()
        this.adaptersMap.clear()

        for ((wallet, adapter) in adaptersMap) {
            val source = wallet.transactionSource
            if (this.adaptersMap.containsKey(source)) continue

            var txAdapter = currentAdapters.remove(source)
            if (txAdapter == null) {
                txAdapter = when (val blockchainType = source.blockchain.type) {
                    BlockchainType.Ethereum,
                    BlockchainType.EthereumGoerli,
                    BlockchainType.BinanceSmartChain,
                    BlockchainType.Polygon,
                    BlockchainType.Avalanche,
                    BlockchainType.Optimism,
                    BlockchainType.Gnosis,
                    BlockchainType.ArbitrumOne -> {
                        adapterFactory.evmTransactionsAdapter(wallet.transactionSource, blockchainType)
                    }
                    BlockchainType.Solana -> {
                        adapterFactory.solanaTransactionsAdapter(wallet.transactionSource)
                    }
                    else -> adapter as? ITransactionsAdapter
                }
            }

            txAdapter?.let {
                this.adaptersMap[source] = it
            }
        }

        currentAdapters.forEach {
            adapterFactory.unlinkAdapter(it.key)
        }

        adaptersReadySubject.onNext(Unit)

    }
}
