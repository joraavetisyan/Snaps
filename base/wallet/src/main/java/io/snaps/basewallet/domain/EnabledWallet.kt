package io.snaps.basewallet.domain

data class EnabledWallet(
    val tokenQueryId: String,
    val coinSettingsId: String,
    val accountId: String,
    val walletOrder: Int? = null,
    val coinName: String? = null,
    val coinCode: String? = null,
    val coinDecimals: Int? = null
)