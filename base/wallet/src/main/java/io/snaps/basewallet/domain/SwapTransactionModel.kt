package io.snaps.basewallet.domain

import java.math.BigInteger

class SwapTransactionModel(
    val address: String,
    val amount: BigInteger,
    val gasPrice: BigInteger,
    val gasLimit: BigInteger,
    val data: ByteArray,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SwapTransactionModel

        if (address != other.address) return false
        if (amount != other.amount) return false
        if (gasPrice != other.gasPrice) return false
        if (gasLimit != other.gasLimit) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + gasPrice.hashCode()
        result = 31 * result + gasLimit.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}