package io.snaps.basewallet.domain

import io.snaps.corecommon.model.CoinValue

data class SnpsAccountModel(
    val unlocked: CoinValue,
    val locked: CoinValue,
    val snpsUsdExchangeRate: Double, // snps/usd
    val bnbUsdExchangeRate: Double, // bnb/usd
) {

    val unlockedInFiat get() = unlocked.toFiat(snpsUsdExchangeRate)
    val lockedInFiat get() = locked.toFiat(snpsUsdExchangeRate)

    // snps/bnb
    val snpsBnbExchangeRate: Double get() = if (bnbUsdExchangeRate > 0) snpsUsdExchangeRate / bnbUsdExchangeRate else 0.0
}