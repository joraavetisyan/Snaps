package io.snaps.corecrypto.entities

enum class BitcoinCashCoinType(val value: String) {
    type0("type0"), type145("type145");

    val title: Int
        get() {
            return when (this) {
                type0 -> 0
                type145 -> 0
            }
        }

    val description: Int
        get() {
            return when (this) {
                type0 -> 0
                type145 -> 0
            }
        }

    companion object {
        private val map = values().associateBy(BitcoinCashCoinType::value)

        fun fromString(value: String?): BitcoinCashCoinType? = map[value]
    }
}