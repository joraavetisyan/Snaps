package io.snaps.basenft.domain

import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.FiatValue
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.Uuid
import java.time.LocalDateTime

data class NftModel(
    val id: Uuid,
    val tokenId: Uuid?,
    val userId: Uuid,
    val type: NftType,
    val image: ImageValue,
    val mintDate: LocalDateTime,
    val dailyReward: CoinValue,
    val dailyUnlock: Double,
    val dailyConsumption: Double,
    val fiatCost: FiatValue?,
    val repairCost: CoinValue,
    val level: Int,
    val experience: Int,
    val lowerThreshold: Int,
    val upperThreshold: Int,
    val bonus: Int,
    val isPurchasable: Boolean,
    val isHealthy: Boolean,
) {

    val displayName: TextValue get() = type.name.textValue()
}