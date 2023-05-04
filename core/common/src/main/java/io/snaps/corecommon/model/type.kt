package io.snaps.corecommon.model

import io.snaps.corecommon.container.ImageValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID
import java.util.Currency

typealias DateTime = String
typealias Timestamp = Long
typealias Token = String
typealias FullUrl = String
typealias DeviceId = String
typealias Uuid = String
typealias CurrencySymbol = String
typealias WalletAddress = String
typealias NftTypeInt = Int

fun generateCurrentDateTime() = ZonedDateTime.now().toString()
fun generateRequestId() = UUID.randomUUID().toString()

@Serializable
enum class FiatCurrency(
    val symbol: CurrencySymbol,
) {
    @SerialName("643") RUB("₽"),
    @SerialName("840") USD("$"),
    @SerialName("978") EUR("€"),
    ;

    val displayName
        get() = runCatching {
            Currency.getInstance(name).displayName
        }.getOrNull().orEmpty()
}

@Serializable
enum class SubsType {
    Subscriptions, Subscribers,
}

@Serializable
data class WalletModel(
    val coinUid: String,
    val receiveAddress: WalletAddress,
    val symbol: String,
    val iconUrl: FullUrl,
    val coinValue: String,
    val fiatValue: String,
    val decimal: Int,
    val coinAddress: String?,
)

@Serializable
enum class SocialNetwork(
    val url: String
) {
    Instagram("com.instagram.android"),
    Facebook("com.facebook.katana"),
    Twitter("com.twitter.android"),
}

@Serializable
enum class QuestType {
    Like,
    PublishVideo,
    Subscribe,
    Watch,
    SocialShare,
    SocialPost,
}

@Serializable
enum class NftType(val intType: NftTypeInt, val storeId: String?) {
    @SerialName("Free") Free(0, null),
    @SerialName("Newbee") Newbie(1, "newbie"),
    @SerialName("Viewer") Viewer(2, "viewer"),
    @SerialName("Follower") Follower(3, "follower"),
    @SerialName("Sub") Sub(4, "sub"),
    @SerialName("Sponsor") Sponsor(5, "sponsor"),
    @SerialName("Influencer") Influencer(6, null),
    @SerialName("FamousGuy") FamousGuy(7, null),
    @SerialName("Star") Star(8, null),
    @SerialName("Rockstar") Rockstar(9, null),
    @SerialName("SuperStar") SuperStar(10, null),
    @SerialName("Legend") Legend(11, null),
    ;

    companion object {

        fun fromIntType(intType: NftTypeInt) = values().first { it.intType == intType }
    }
}

enum class OnboardingType {
    Rank,
    Popular,
    Tasks,
    Nft,
    Referral,
    Wallet,
    Rewards,
}

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
    val costInUsd: Int?,
    val costInRealTokens: Int?,
    val mintedDate: LocalDateTime,
    val isHealthy: Boolean,
    val repairCost: Double,
)