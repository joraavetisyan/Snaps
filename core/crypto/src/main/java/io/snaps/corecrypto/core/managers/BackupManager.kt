package io.snaps.corecrypto.core.managers

import io.snaps.corecrypto.core.IAccountManager
import io.snaps.corecrypto.core.IBackupManager
import io.reactivex.Flowable

class BackupManager(private val accountManager: IAccountManager) : IBackupManager {

    override val allBackedUp: Boolean
        get() = accountManager.accounts.all { it.isBackedUp }

    override val allBackedUpFlowable: Flowable<Boolean>
        get() = accountManager.accountsFlowable.map { accounts ->
            accounts.all { it.isBackedUp }
        }
}
