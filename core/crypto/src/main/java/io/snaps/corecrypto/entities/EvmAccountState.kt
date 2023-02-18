package io.snaps.corecrypto.entities

import androidx.room.Entity

@Entity(primaryKeys = ["accountId", "chainId"])
data class EvmAccountState(
    val accountId: String,
    val chainId: Int,
    val transactionsSyncedBlockNumber: Long,
    val restored: Boolean = false
)
