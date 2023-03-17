package io.snaps.corecrypto.core.factories

import io.horizontalsystems.marketkit.models.BlockchainType
import io.snaps.corecrypto.core.IAccountManager
import io.snaps.corecrypto.core.IWalletManager
import io.snaps.corecrypto.core.managers.EvmAccountManager
import io.snaps.corecrypto.core.managers.EvmKitManager
import io.snaps.corecrypto.core.managers.MarketKitWrapper
import io.snaps.corecrypto.core.storage.EvmAccountStateDao

class EvmAccountManagerFactory(
    private val accountManager: IAccountManager,
    private val walletManager: IWalletManager,
    private val marketKit: MarketKitWrapper,
    private val evmAccountStateDao: EvmAccountStateDao
) {

    fun evmAccountManager(blockchainType: BlockchainType, evmKitManager: EvmKitManager) =
        EvmAccountManager(
            blockchainType,
            accountManager,
            walletManager,
            marketKit,
            evmKitManager,
            evmAccountStateDao
        )

}
