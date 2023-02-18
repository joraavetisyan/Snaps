package io.snaps.corecrypto.walletconnect.other

import androidx.annotation.StringRes

enum class BalanceViewType(@StringRes val titleResId: Int, @StringRes val subtitleResId: Int) {
    CoinThenFiat(0, 0),
    FiatThenCoin(0, 0);
}
