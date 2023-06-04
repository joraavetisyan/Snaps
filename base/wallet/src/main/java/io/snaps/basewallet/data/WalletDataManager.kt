package io.snaps.basewallet.data

import io.snaps.corecommon.model.Uuid
import io.snaps.corecrypto.core.IAccountManager
import io.snaps.corecrypto.core.IWalletManager
import javax.inject.Inject

interface WalletDataManager {

    fun hasAccount(userId: Uuid): Boolean

    fun clear(userId: Uuid)
}

class WalletDataManagerImpl @Inject constructor(
    private val walletManager: IWalletManager,
    private val accountManager: IAccountManager,
) : WalletDataManager {

    override fun hasAccount(userId: Uuid): Boolean {
        return accountManager.account(userId) != null
    }

    override fun clear(userId: Uuid) {
        // todo full clear, without depending on userId
        accountManager.activeAccount?.let { walletManager.delete(walletManager.getWallets(it)) }
        accountManager.delete(userId)
    }
}