package io.snaps.basewallet.domain

data class TotalBalanceModel(
    val coin: String,
    val fiat: String,
) {

    companion object {

        val empty: TotalBalanceModel = TotalBalanceModel("", "")
    }
}