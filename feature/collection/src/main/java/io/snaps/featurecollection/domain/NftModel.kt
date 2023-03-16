package io.snaps.featurecollection.domain

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.Uuid
import java.time.LocalDateTime

data class NftModel(
    val id: Uuid,
    val tokenId: Uuid?,
    val userId: Uuid,
    val type: NftType,
    val image: ImageValue,
    val dailyReward: Int,
    val dailyUnlock: Double,
    val dailyConsumption: Double,
    val isAvailableToPurchase: Boolean,
    val costInUsd: Int,
    val costInRealTokens: Int,
    val mintedDate: LocalDateTime,
    val isHealthy: Boolean,
)