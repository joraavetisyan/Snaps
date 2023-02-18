package io.snaps.corecrypto.other

sealed class BalanceSortType {
    object Name : BalanceSortType()
    object Value : BalanceSortType()
    object PercentGrowth : BalanceSortType()

    fun getTitleRes(): Int = when (this) {
        Value -> 0
        Name -> 0
        PercentGrowth -> 0
    }

    fun getAsString(): String = when (this) {
        Value -> "value"
        Name -> "name"
        PercentGrowth -> "percent_growth"
    }

    companion object {
        fun getTypeFromString(value: String): BalanceSortType = when (value) {
            "value" -> Value
            "percent_growth" -> PercentGrowth
            else -> Name
        }
    }
}