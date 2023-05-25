@file:Suppress("FunctionName")

package io.snaps.corecommon.model

import io.snaps.corecommon.BuildConfig
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.ext.toMoneyFormat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
typealias TxHash = String // blockchain transaction hash in hex format
typealias TxSign = String // blockchain transaction signature in hex format
typealias NftTypeInt = Int

fun generateCurrentDateTime() = ZonedDateTime.now().toString()
fun generateUuid() = UUID.randomUUID().toString()

interface Money {

    val symbol: String
}

@Serializable
enum class FiatCurrency(
    override val symbol: MoneySymbol,
) : Money {
    @SerialName("643") RUB("₽"),
    @SerialName("840") USD("$"),
    @SerialName("978") EUR("€"),
    ;

    val displayName
        get() = runCatching { java.util.Currency.getInstance(name).displayName }.getOrNull() ?: name
}

@Serializable
enum class CoinType(
    override val symbol: String,
    val coinName: String,
    val code: String,
    val address: CryptoAddress,
    val decimal: Int,
) : Money {
    // todo release
    SNPS(
        "SNPS",
        "Snaps",
        "SNAPS",
        if (!BuildConfig.DEBUG) "0x933bcd9a03d350f040d2fe7e36d60a9c73d42ef5" else "0x92677918569A2BEA213Af66b54e0C9B9811d021c",
        18,
    ),
    BNB("BNB", "BNB", "BNB", "0x242a1ff6ee06f2131b7924cacb74c7f9e3a5edc9", 18),
    // WBNB("WBNB","WBNB", "WBNB", "0xbb4CdB9CBd36B01bD1cBaEBF2De08d9173bc095c", 18),
    BUSD("BUSD", "BUSD", "BUSD", "0xe9e7cea3dedca5984780bafc599bd69add087d56", 18),
    USDT("USDT", "Tether USD", "USDT", "0x55d398326f99059ff775485246999027b3197955", 18),
    ;

    companion object {

        fun byAddress(address: CryptoAddress) = values().find { it.address == address }
    }
}

@Serializable
data class FiatValue(
    @SerialName("currency") val currency: FiatCurrency,
    @SerialName("value") val value: Double,
) {

    companion object {

        const val decimals = 2

        val default = FiatCurrency.USD
    }

    fun getFormattedValue() = value.toMoneyFormat(decimals)

    fun getFormatted(currencyFirst: Boolean = false) = "%s %s".run {
        if (currencyFirst) format(currency.symbol, getFormattedValue())
        else format(getFormattedValue(), currency.symbol)
    }

    operator fun plus(other: FiatValue): FiatValue {
        require(this.currency == other.currency)
        return copy(value = this.value + other.value)
    }
}

fun FiatRUB(value: Double) = FiatValue(FiatCurrency.RUB, value)
fun FiatUSD(value: Double) = FiatValue(FiatCurrency.USD, value)
fun FiatEUR(value: Double) = FiatValue(FiatCurrency.EUR, value)

@Serializable
data class CoinValue(
    val type: CoinType,
    val value: Double,
) {

    companion object {

        private const val significantFigureCount = 3

        val native = CoinType.BNB
    }

    fun getFormattedValue() = value.toMoneyFormat(calculateRoundPlaces())

    private fun calculateRoundPlaces(): Int {
        val string = value.toBigDecimal().toPlainString()
        var count = significantFigureCount
        val pointIndex = string.indexOf('.')
        if (pointIndex != -1) for (i in pointIndex + 1..string.lastIndex) {
            if (string[i] == '0') count++ else break
        }
        return count
    }

    fun getFormatted() = "%s %s".format(getFormattedValue(), type.symbol)

    fun toFiat(rate: Double, currency: FiatCurrency = FiatValue.default): FiatValue {
        log("$type to $currency with rate $rate")
        return FiatValue(currency, value * rate)
    }

    fun toCoin(rate: Double, type: CoinType = native): CoinValue {
        log("${this.type} to $type with rate $rate")
        return CoinValue(type, value * rate)
    }

    operator fun plus(other: CoinValue): CoinValue {
        require(this.type == other.type)
        return copy(value = this.value + other.value)
    }
}

fun CoinSNPS(value: Double) = CoinValue(CoinType.SNPS, value)
fun CoinBUSD(value: Double) = CoinValue(CoinType.BUSD, value)
fun CoinUSDT(value: Double) = CoinValue(CoinType.USDT, value)
fun CoinBNB(value: Double) = CoinValue(CoinType.BNB, value)

@Serializable
enum class Nft(val address: CryptoAddress) {
    // todo release
    SNAPS(if (!BuildConfig.DEBUG) "0xbeBaaBA9056B135f50A90e86695e4665E4e33201" else "0x5F0cF62ad1DD5A267427DC161ff365b75142E3b3")
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

    val displayName get() = name

    companion object {

        fun byIntType(intType: NftTypeInt) = values().first { it.intType == intType }
    }
}