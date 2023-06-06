package io.snaps.basenft.ui

import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.FiatValue
import io.snaps.corecommon.strings.StringKey

fun FiatValue?.rankCostToString(): TextValue = when {
    this == null -> "".textValue()
    this.value == 0.0 -> StringKey.PurchaseFieldCostFree.textValue()
    else -> getFormatted().textValue()
}