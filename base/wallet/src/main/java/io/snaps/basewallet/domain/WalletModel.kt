package io.snaps.basewallet.domain

import io.snaps.corecommon.model.FullUrl

data class WalletModel(
    val symbol: String,
    val iconUrl: FullUrl,
)