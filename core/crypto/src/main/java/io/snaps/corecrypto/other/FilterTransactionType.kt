package io.snaps.corecrypto.other

import io.horizontalsystems.marketkit.models.Blockchain
import io.horizontalsystems.marketkit.models.Token
import io.snaps.corecrypto.core.HSCaution
import io.snaps.corecrypto.entities.Account
import io.snaps.corecrypto.entities.CoinSettings
import io.snaps.corecrypto.entities.CoinValue
import io.snaps.corecrypto.entities.CurrencyValue
import java.math.BigDecimal
import java.util.Date

enum class FilterTransactionType {
    All, Incoming, Outgoing, Swap, Approve;

    val title: Int
        get() = when (this) {
            All -> 0
            Incoming -> 0
            Outgoing -> 0
            Swap -> 0
            Approve -> 0
        }
}

data class TransactionLockInfo(
    val lockedUntil: Date,
    val originalAddress: String,
    val amount: BigDecimal?
)

sealed class TransactionStatus {
    object Pending : TransactionStatus()
    class Processing(val progress: Float) : TransactionStatus() //progress in 0.0 .. 1.0
    object Completed : TransactionStatus()
    object Failed : TransactionStatus()
}

data class TransactionWallet(
    val token: Token?,
    val source: TransactionSource,
    val badge: String?
)

data class TransactionSource(
    val blockchain: Blockchain,
    val account: Account,
    val coinSettings: CoinSettings
)

object SendModule {

    data class AmountData(val primary: AmountInfo, val secondary: AmountInfo?) {
        fun getFormatted(): String {
            var formatted = primary.getFormattedPlain()

            secondary?.let {
                formatted += "  |  " + it.getFormattedPlain()
            }

            return formatted
        }
    }

    sealed class AmountInfo {
        data class CoinValueInfo(val coinValue: CoinValue) : AmountInfo()
        data class CurrencyValueInfo(val currencyValue: CurrencyValue) : AmountInfo()

        val value: BigDecimal
            get() = when (this) {
                is CoinValueInfo -> coinValue.value
                is CurrencyValueInfo -> currencyValue.value
            }

        val decimal: Int
            get() = when (this) {
                is CoinValueInfo -> coinValue.decimal
                is CurrencyValueInfo -> currencyValue.currency.decimal
            }

        fun getAmountName(): String = when (this) {
            is CoinValueInfo -> coinValue.coin.name
            is CurrencyValueInfo -> currencyValue.currency.code
        }

        fun getFormatted(): String = when (this) {
            is CoinValueInfo -> coinValue.getFormattedFull()
            is CurrencyValueInfo -> """App.numberFormatter.formatFiatFull(
                currencyValue.value, currencyValue.currency.symbol
            )"""
        }

        fun getFormattedPlain(): String = when (this) {
            is CoinValueInfo -> {
                "App.numberFormatter.format(value, 0, 8)"
            }
            is CurrencyValueInfo -> {
                """App.numberFormatter.formatFiatFull(
                    currencyValue.value,
                    currencyValue.currency.symbol
                )"""
            }
        }

    }

}

sealed class SendResult {
    object Sending : SendResult()
    object Sent : SendResult()
    class Failed(val caution: HSCaution) : SendResult()
}


