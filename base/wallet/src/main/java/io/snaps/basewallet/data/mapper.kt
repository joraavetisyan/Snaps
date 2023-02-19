package io.snaps.basewallet.data

import io.horizontalsystems.marketkit.models.Coin
import io.snaps.basewallet.domain.WalletModel
import io.snaps.corecrypto.entities.Wallet

private val Coin.iconUrl: String
    get() = "https://cdn.blocksdecoded.com/coin-icons/32px/$uid@3x.png"

fun List<Wallet>.toWalletModelList() = map {
    it.toWalletModel()
}

fun Wallet.toWalletModel() = WalletModel(coin.code, coin.iconUrl)