package io.snaps.basewallet.domain

import io.snaps.corecommon.model.CoinBNB
import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.FiatUSD
import io.snaps.corecommon.model.FiatValue

data class TotalBalanceModel(
    val coin: CoinValue,
    val fiat: FiatValue,
) {

    companion object {

        val empty: TotalBalanceModel = TotalBalanceModel(CoinBNB(0.0), FiatUSD(0.0))
    }
}