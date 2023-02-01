package com.defince.featuremain.domain

import com.defince.corecommon.container.ImageValue

data class Sub(
    val image: ImageValue,
    val name: String,
    val isSubscribed: Boolean,
)