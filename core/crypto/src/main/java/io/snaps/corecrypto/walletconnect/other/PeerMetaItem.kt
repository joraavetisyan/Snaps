package io.snaps.corecrypto.walletconnect.other

import io.snaps.corecrypto.walletconnect.version1.WC1Request

data class PeerMetaItem(
    val name: String,
    val url: String,
    val description: String?,
    val icon: String?,
    val accountName: String?,
)

data class WCRequestWrapper(
    val wC1Request: WC1Request,
    val dAppName: String?
)