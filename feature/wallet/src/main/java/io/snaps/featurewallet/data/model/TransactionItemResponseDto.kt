package io.snaps.featurewallet.data.model

import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TransactionItemResponseDto(
    @SerialName("id") val id: Uuid,
    @SerialName("date") val date: DateTime,
    @SerialName("symbol") val symbol: String,
    @SerialName("iconUrl") val iconUrl: FullUrl,
    @SerialName("coinValue") val coinValue: String,
)

@Serializable
enum class TransactionType {
    All,
    BNB,
}