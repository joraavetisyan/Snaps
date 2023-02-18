package io.snaps.corecrypto.entities

enum class TransactionDataSortMode(val raw: String) {
    Shuffle("shuffle"),
    Bip69("bip69");

    val title: Int
        get() = when (this) {
            Shuffle -> 0
            Bip69 -> 0
        }

    val description: Int
        get() = when (this) {
            Shuffle -> 0
            Bip69 -> 0
        }

}
