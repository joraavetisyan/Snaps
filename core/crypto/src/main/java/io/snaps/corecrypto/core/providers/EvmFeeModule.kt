package io.snaps.corecrypto.core.providers

import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.GasPrice
import io.horizontalsystems.ethereumkit.models.TransactionData
import io.horizontalsystems.marketkit.models.BlockchainType
import io.reactivex.Observable
import io.snaps.corecrypto.core.Warning
import io.snaps.corecrypto.entities.DataState
import io.snaps.corecrypto.entities.FeePriceScale
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

interface IEvmFeeService {
    val transactionStatus: DataState<Transaction>
    val transactionStatusObservable: Observable<DataState<Transaction>>
}

interface IEvmGasPriceService {
    val state: DataState<GasPriceInfo>
    val stateObservable: Observable<DataState<GasPriceInfo>>
    val isRecommendedGasPriceSelected: Boolean
}

abstract class FeeSettingsError : Throwable() {
    object InsufficientBalance : FeeSettingsError()
    object LowMaxFee : FeeSettingsError()
    class InvalidGasPriceType(override val message: String) : FeeSettingsError()
}

abstract class FeeSettingsWarning : Warning() {
    object RiskOfGettingStuck : FeeSettingsWarning()
    object Overpricing : FeeSettingsWarning()
}

data class GasPriceInfo(
    val gasPrice: GasPrice,
    val warnings: List<Warning>,
    val errors: List<Throwable>
)

open class GasData(val gasLimit: Long, val gasPrice: GasPrice) {

    open val fee: BigInteger
        get() = gasLimit.toBigInteger() * gasPrice.max.toBigInteger()

}

class RollupGasData(gasLimit: Long, gasPrice: GasPrice, val l1Fee: BigInteger) :
    GasData(gasLimit, gasPrice) {

    override val fee: BigInteger
        get() = super.fee + l1Fee

}

data class Transaction(
    val transactionData: TransactionData,
    val gasData: GasData,
    val warnings: List<Warning> = listOf(),
    val errors: List<Throwable> = listOf()
) {
    val totalAmount: BigInteger
        get() = transactionData.value + gasData.fee
}

data class FeeRangeConfig(
    val lowerBound: Bound,
    val upperBound: Bound
) {
    sealed class Bound {
        class Fixed(val value: Long) : Bound()
        class Multiplied(val multiplier: BigDecimal) : Bound()
        class Added(val addend: Long) : Bound()

        fun calculate(selectedValue: Long) = when (this) {
            is Added -> selectedValue + addend
            is Fixed -> value
            is Multiplied -> {
                (BigDecimal(selectedValue) * multiplier)
                    .setScale(0, RoundingMode.HALF_UP)
                    .toLong()
            }
        }
    }
}

sealed class GasDataError : Error() {
    object NoTransactionData : GasDataError()
    object InsufficientBalance : GasDataError()
}

data class FeeViewItem(val fee: String, val gasLimit: String)

data class SliderViewItem(
    private val initialWeiValue: Long,
    private val weiRange: LongRange,
    private val stepSize: Long,
    private val scale: FeePriceScale
) {

    val initialSliderValue: Long = sliderValue(initialWeiValue)
    val range: LongRange = LongRange(sliderValue(weiRange.first), sliderValue(weiRange.last))

    fun wei(sliderValue: Long): Long {
        return sliderValue * stepSize
    }

    fun sliderValue(wei: Long): Long {
        return wei / stepSize
    }
}

internal val BlockchainType.l1GasFeeContractAddress: Address?
    get() =
        when (this) {
            BlockchainType.Optimism -> Address("0x420000000000000000000000000000000000000F")
            else -> null
        }
