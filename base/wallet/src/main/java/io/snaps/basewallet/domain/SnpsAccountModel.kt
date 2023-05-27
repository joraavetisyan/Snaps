package io.snaps.basewallet.domain

import io.snaps.corecommon.model.CoinValue

// rate eg: snpsUsd... -> how much usd in 1 snps
data class SnpsAccountModel( // todo rename
    val unlocked: CoinValue,
    val locked: CoinValue,
    val snpsUsdExchangeRate: Double,
    val bnbUsdExchangeRate: Double,
) {

    val unlockedInFiat get() = unlocked.toFiat(snpsUsdExchangeRate)
    val lockedInFiat get() = locked.toFiat(snpsUsdExchangeRate)

    val snpsBnbExchangeRate: Double get() = if (bnbUsdExchangeRate > 0) {
        snpsUsdExchangeRate / bnbUsdExchangeRate
    } else {
        0.0
    }

    val usdBnbExchangeRate: Double get() = if (bnbUsdExchangeRate > 0) {
        1.0 / bnbUsdExchangeRate
    } else {
        0.0
    }
}