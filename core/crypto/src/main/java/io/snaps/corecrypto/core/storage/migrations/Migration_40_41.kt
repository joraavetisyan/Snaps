package io.snaps.corecrypto.core.storage.migrations

private enum class BtcBlockchain(val raw: String) {
    Bitcoin("bitcoin"),
    BitcoinCash("bitcoinCash"),
    Litecoin("litecoin"),
    Dash("dash");
}