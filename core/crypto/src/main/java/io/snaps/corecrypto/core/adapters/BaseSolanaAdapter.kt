package io.snaps.corecrypto.core.adapters

import io.horizontalsystems.solanakit.Signer
import io.snaps.corecrypto.core.IAdapter
import io.snaps.corecrypto.core.IBalanceAdapter
import io.snaps.corecrypto.core.IReceiveAdapter
import io.snaps.corecrypto.core.managers.SolanaKitWrapper

abstract class BaseSolanaAdapter(
        solanaKitWrapper: SolanaKitWrapper,
        val decimal: Int
) : IAdapter, IBalanceAdapter, IReceiveAdapter {

    val solanaKit = solanaKitWrapper.solanaKit
    protected val signer: Signer? = solanaKitWrapper.signer

    override val isMainnet: Boolean
        get() = solanaKit.isMainnet

    override val debugInfo: String
        get() = solanaKit.debugInfo()

    // IReceiveAdapter

    override val receiveAddress: String
        get() = solanaKit.receiveAddress

    companion object {
        const val confirmationsThreshold: Int = 12
    }

}
