package io.snaps.corecommon.model

import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.ext.formatToMoney
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

typealias DateTime = String
typealias Timestamp = Long
typealias Token = String
typealias FullUrl = String
typealias DeviceId = String
typealias Uuid = String
typealias CardNumber = String
typealias MoneySymbol = String
typealias CryptoAddress = String
typealias TxHash = String // blockchain transaction hash
typealias NftTypeInt = Int

fun generateCurrentDateTime() = ZonedDateTime.now().toString()
fun generateUuid() = UUID.randomUUID().toString()

interface Money {

    val symbol: String
}

@Serializable
data class Fiat(
    @SerialName("currency") val currency: Currency,
    @SerialName("value") val value: Double,
) {

    @Serializable
    enum class Currency(
        override val symbol: MoneySymbol,
    ) : Money {
        @SerialName("643") RUB("₽"),
        @SerialName("840") USD("$"),
        @SerialName("978") EUR("€"),
        ;

        val displayName
            get() = runCatching { java.util.Currency.getInstance(name).displayName }.getOrNull() ?: name
    }

    fun getFormattedMoney() = value.formatToMoney()

    fun getFormattedMoneyWithCurrency(currencyFirst: Boolean = true) = "%s %s".run {
        if (currencyFirst) format(currency.symbol, getFormattedMoney())
        else format(getFormattedMoney(), currency.symbol)
    }
}

@Serializable
data class Coin(
    val type: Type,
    val value: Double,
) {

    @Serializable
    enum class Type(
        override val symbol: String,
        val coinName: String,
        val code: String,
        val address: CryptoAddress,
        val decimal: Int,
    ) : Money {
        SNPS("SNPS", "Snaps", "SNAPS", "0x92677918569A2BEA213Af66b54e0C9B9811d021c", 18),
        // WBNB("WBNB","WBNB", "WBNB", "0xbb4CdB9CBd36B01bD1cBaEBF2De08d9173bc095c", 18),
        BUSD("BUSD", "BUSD", "BUSD", "0xe9e7cea3dedca5984780bafc599bd69add087d56", 18),
        USDT("USDT", "Tether USD", "USDT", "0x55d398326f99059ff775485246999027b3197955", 18),
        BNB("BNB", "BNB", "BNB", "0x242a1ff6ee06f2131b7924cacb74c7f9e3a5edc9", 18),
    }

    fun getFormattedValue() = value.formatToMoney()

    fun getFormattedValueWithType() = "%s %s".format(getFormattedValue(), type.symbol)
}

@Serializable
enum class Nft(val address: CryptoAddress) {
    SNAPS("0x5F0cF62ad1DD5A267427DC161ff365b75142E3b3")
}

@Serializable
enum class SubsType {
    Subscriptions,
    Subscribers,
}

@Serializable
enum class TaskType {
    Like,
    PublishVideo,
    Subscribe,
    Watch,
    SocialShare,
    SocialPost,
}

@Serializable
enum class OnboardingType {
    Rank,
    Popular,
    Tasks,
    Nft,
    Referral,
    Wallet,
    Rewards,
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

    fun getSunglassesImage() = when (this) {
        Free -> R.drawable.img_sunglasses0
        Newbie -> R.drawable.img_sunglasses1
        Viewer -> R.drawable.img_sunglasses2
        Follower -> R.drawable.img_sunglasses3
        Sub -> R.drawable.img_sunglasses4
        Sponsor -> R.drawable.img_sunglasses5
        Influencer -> R.drawable.img_sunglasses6
        FamousGuy -> R.drawable.img_sunglasses7
        Star -> R.drawable.img_sunglasses8
        Rockstar -> R.drawable.img_sunglasses9
        SuperStar -> R.drawable.img_sunglasses10
        Legend -> R.drawable.img_sunglasses10
    }.imageValue()

    companion object {

        fun fromIntType(intType: NftTypeInt) = values().first { it.intType == intType }
    }
}

@Serializable
data class WalletModel(
    val coinUid: String,
    val receiveAddress: CryptoAddress,
    val symbol: String,
    val iconUrl: FullUrl,
    val coinValue: String,
    val fiatValue: String,
    val decimal: Int,
    val coinAddress: String?,
) {

    val coinValueDouble: Double
        get() = coinValue.replace(",", ".")
            .filter { it.isDigit() || it == '.' }
            .toDoubleOrNull() ?: 0.0
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
    val isProcessing: Boolean,
    val level: Int,
    val experience: Int,
    val lowerThreshold: Int,
    val upperThreshold: Int,
    val bonus: Int,
)