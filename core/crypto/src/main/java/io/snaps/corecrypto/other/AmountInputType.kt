package io.snaps.corecrypto.other

enum class AmountInputType {
    COIN, CURRENCY;

    fun reversed(): AmountInputType {
        return if (this == COIN) CURRENCY else COIN
    }
}
