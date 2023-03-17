package io.snaps.corecrypto.walletconnect.version1

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.snaps.corecrypto.core.IAccountManager
import io.snaps.corecrypto.core.managers.EvmSyncSourceManager
import io.snaps.corecrypto.core.subscribeIO
import io.snaps.corecrypto.walletconnect.entity.WalletConnectSession
import io.snaps.corecrypto.walletconnect.storage.WC1SessionStorage

class WC1SessionManager(
    private val storage: WC1SessionStorage,
    private val accountManager: IAccountManager,
    evmSyncSourceManager: EvmSyncSourceManager
) {
    private val disposable = CompositeDisposable()

    private val sessionsSubject = PublishSubject.create<List<WalletConnectSession>>()
    val sessionsObservable: Flowable<List<WalletConnectSession>>
        get() = sessionsSubject.toFlowable(BackpressureStrategy.BUFFER)

    val sessions: List<WalletConnectSession>
        get() = accountManager.activeAccount?.let { storage.getSessions(it.id) } ?: listOf()

    init {
        accountManager.accountsDeletedFlowable
                .subscribeIO {
                    handleDeletedAccounts()
                }
                .let {
                    disposable.add(it)
                }

        accountManager.activeAccountObservable
                .subscribeIO {
                    handleActiveAccount()
                }
                .let {
                    disposable.add(it)
                }

        evmSyncSourceManager.syncSourceObservable
            .subscribeIO {
                syncSessions()
            }
            .let {
                disposable.add(it)
            }
    }

    private fun handleActiveAccount() {
        syncSessions()
    }

    fun save(session: WalletConnectSession) {
        storage.save(session)
        syncSessions()
    }

    fun deleteSession(peerId: String) {
        storage.deleteSessionsByPeerId(peerId)
        syncSessions()
    }

    private fun handleDeletedAccounts() {
        val existingAccountIds = accountManager.accounts.map { it.id }
        storage.deleteSessionsExcept(accountIds = existingAccountIds)

        syncSessions()
    }

    private fun syncSessions() {
        sessionsSubject.onNext(sessions)
    }

}
