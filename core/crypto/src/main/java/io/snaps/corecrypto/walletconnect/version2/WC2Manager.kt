package io.snaps.corecrypto.walletconnect.version2

import io.snaps.corecrypto.core.IAccountManager
import io.snaps.corecrypto.core.managers.EvmBlockchainManager
import io.snaps.corecrypto.core.managers.EvmKitWrapper
import io.snaps.corecrypto.entities.Account

class WC2Manager(
        private val accountManager: IAccountManager,
        private val evmBlockchainManager: EvmBlockchainManager
) {

    val activeAccount: Account?
        get() = accountManager.activeAccount

    fun getEvmKitWrapper(chainId: Int, account: Account): EvmKitWrapper? {
        val blockchain = evmBlockchainManager.getBlockchain(chainId) ?: return null
        val evmKitManager = evmBlockchainManager.getEvmKitManager(blockchain.type)
        val evmKitWrapper = evmKitManager.getEvmKitWrapper(account, blockchain.type)

        return if (evmKitWrapper.evmKit.chain.id == chainId) {
            evmKitWrapper
        } else {
            evmKitManager.unlink(account)
            null
        }
    }

}
