package io.snaps.basewallet.domain

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.CoinType
import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.corecommon.model.FiatValue

data class WalletModel(
    val coinUid: String,
    val coinType: CoinType,
    val receiveAddress: CryptoAddress,
    val image: ImageValue,
    val coinValue: CoinValue,
    val fiatValue: FiatValue,
)