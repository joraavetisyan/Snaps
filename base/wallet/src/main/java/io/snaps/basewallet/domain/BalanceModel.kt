package io.snaps.basewallet.domain

import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.FiatUSD

data class BalanceModel(
    val unlocked: CoinValue,
    val locked: CoinValue,
    val snpExchangeRate: Double,
    val bnbExchangeRate: Double,
) {

    val unlockedInFiat get() = FiatUSD(unlocked.value * snpExchangeRate)
    val lockedInFiat get() = FiatUSD(locked.value * snpExchangeRate)
}