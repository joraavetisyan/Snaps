package io.snaps.corecrypto.core.providers

import io.horizontalsystems.marketkit.models.CoinPrice
import io.snaps.corecrypto.core.AdapterState
import io.snaps.corecrypto.core.BalanceData
import io.snaps.corecrypto.entities.Wallet

data class BalanceItem(
    val wallet: Wallet,
    val mainNet: Boolean,
    val balanceData: BalanceData,
    val state: AdapterState,
    val coinPrice: CoinPrice? = null
) {
    val fiatValue get() = coinPrice?.value?.let { balanceData.available.times(it) }
    val balanceFiatTotal get() = coinPrice?.value?.let { balanceData.total.times(it) }
}