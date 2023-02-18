package io.snaps.corecrypto.entities.nft

import io.snaps.corecrypto.entities.Account
import io.horizontalsystems.marketkit.models.BlockchainType

data class NftKey(
    val account: Account,
    val blockchainType: BlockchainType
)