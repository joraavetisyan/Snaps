package io.snaps.basebilling.model

import com.android.billingclient.api.SkuDetails

data class Product(
    val details: SkuDetails,
)