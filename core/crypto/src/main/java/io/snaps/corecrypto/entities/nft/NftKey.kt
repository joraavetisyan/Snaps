package io.snaps.corecrypto.entities.nft

import io.horizontalsystems.marketkit.models.BlockchainType
import io.snaps.corecrypto.entities.Account

data class NftKey(
    val account: Account,
    val blockchainType: BlockchainType
)