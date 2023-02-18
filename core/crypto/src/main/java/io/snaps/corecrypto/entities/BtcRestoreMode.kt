package io.snaps.corecrypto.entities

enum class BtcRestoreMode(val raw: String) {
    Api("api"),
    Blockchain("blockchain");

    val title: Int
        get() = when (this) {
            Api -> 0
            Blockchain -> 0
        }

    val description: Int
        get() = when (this) {
            Api -> 0
            Blockchain -> 0
        }

}
