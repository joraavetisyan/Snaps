package io.snaps.corecrypto.core.managers

import io.snaps.corecrypto.core.IAccountManager
import io.snaps.corecrypto.core.ILocalStorage
import io.snaps.corecrypto.core.IWalletManager
import io.snaps.corecrypto.other.IKeyStoreCleaner

class KeyStoreCleaner(
    private val localStorage: ILocalStorage,
    private val accountManager: IAccountManager,
    private val walletManager: IWalletManager
) : IKeyStoreCleaner {

    override var encryptedSampleText: String?
        get() = localStorage.encryptedSampleText
        set(value) {
            localStorage.encryptedSampleText = value
        }

    override fun cleanApp() {
        accountManager.clear()
        walletManager.clear()
        localStorage.clear()
    }
}
