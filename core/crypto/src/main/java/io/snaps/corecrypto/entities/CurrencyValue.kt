package io.snaps.corecrypto.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class CurrencyValue(val currency: Currency, val value: BigDecimal) : Parcelable {
    fun getFormattedFull(): String {
        return ""
    }

    fun getFormattedShort(): String {
        return ""
    }
}
