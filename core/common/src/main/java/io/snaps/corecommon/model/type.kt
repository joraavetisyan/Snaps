package io.snaps.corecommon.model

import io.snaps.corecommon.ext.formatToMoney
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime
import java.util.UUID
import java.util.Currency

typealias DateTime = String
typealias Timestamp = Long
typealias Token = String
typealias PhoneNumber = String
typealias FormattedMoneyWithCurrency = String
typealias FormattedMoney = String
typealias CurrencyIso = Int
typealias FullUrl = String
typealias PartialUrl = String
typealias DeviceId = String
typealias Uuid = String
typealias CurrencySymbol = String
typealias WalletAddress = String

fun generateCurrentDateTime() = ZonedDateTime.now().toString()
fun generateRequestId() = UUID.randomUUID().toString()

@Serializable
data class PhoneNumberData(
    val code: Int, // eg 7
    val number: Long, // eg 1234567890
) {

    fun getPhoneNumber(): PhoneNumber = "$code$number"
    fun getPhoneNumberWithPlus(): PhoneNumber = "+${getPhoneNumber()}"
}

@Serializable
data class MoneyDto(
    @SerialName("currency") val currency: FiatCurrency,
    @SerialName("value") val value: Double,
) {

    fun getFormattedMoney() = value.formatToMoney()

    fun getFormattedMoneyWithCurrency() = "%s %s".format(getFormattedMoney(), currency.symbol)
}

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
    @SerialName("coinUid") val coinUid: String,
    @SerialName("receiveAddress") val receiveAddress: WalletAddress,
    @SerialName("symbol") val symbol: String,
    @SerialName("iconUrl") val iconUrl: FullUrl,
    @SerialName("coinValue") val coinValue: String,
    @SerialName("fiatValue") val fiatValue: String,
)