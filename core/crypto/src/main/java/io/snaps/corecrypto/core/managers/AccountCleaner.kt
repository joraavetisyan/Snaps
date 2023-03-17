package io.snaps.corecrypto.core.managers

import io.snaps.corecrypto.core.IAccountCleaner
import io.snaps.corecrypto.core.adapters.BinanceAdapter
import io.snaps.corecrypto.core.adapters.BitcoinAdapter
import io.snaps.corecrypto.core.adapters.BitcoinCashAdapter
import io.snaps.corecrypto.core.adapters.DashAdapter
import io.snaps.corecrypto.core.adapters.Eip20Adapter
import io.snaps.corecrypto.core.adapters.EvmAdapter
import io.snaps.corecrypto.core.adapters.SolanaAdapter
import io.snaps.corecrypto.core.adapters.zcash.ZcashAdapter

class AccountCleaner(private val testMode: Boolean) : IAccountCleaner {

    override fun clearAccounts(accountIds: List<String>) {
        accountIds.forEach { clearAccount(it) }
    }

    private fun clearAccount(accountId: String) {
        BinanceAdapter.clear(accountId, testMode)
        BitcoinAdapter.clear(accountId, testMode)
        BitcoinCashAdapter.clear(accountId, testMode)
        DashAdapter.clear(accountId, testMode)
        EvmAdapter.clear(accountId, testMode)
        Eip20Adapter.clear(accountId, testMode)
        ZcashAdapter.clear(accountId, testMode)
        SolanaAdapter.clear(accountId, testMode)
    }

}
