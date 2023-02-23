package io.snaps.corecrypto.core.providers

import io.reactivex.Observable
import io.snaps.corecrypto.core.IWalletManager
import io.snaps.corecrypto.core.managers.EvmSyncSourceManager
import io.snaps.corecrypto.entities.Wallet

class BalanceActiveWalletRepository(
    private val walletManager: IWalletManager,
    evmSyncSourceManager: EvmSyncSourceManager,
) {

    val itemsObservable: Observable<List<Wallet>> =
        Observable
            .merge(
                Observable.just(Unit),
                walletManager.activeWalletsUpdatedObservable,
                evmSyncSourceManager.syncSourceObservable
            )
            .map {
                walletManager.activeWallets
            }

    fun disable(wallet: Wallet) {
        walletManager.delete(listOf(wallet))
    }

    fun enable(wallet: Wallet) {
        walletManager.save(listOf(wallet))
    }
}
