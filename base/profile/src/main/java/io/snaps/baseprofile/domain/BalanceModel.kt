package io.snaps.baseprofile.domain

data class BalanceModel(
    val unlocked: Double,
    val locked: Double,
    val snpExchangeRate: Double,
    val bnbExchangeRate: Double,
)