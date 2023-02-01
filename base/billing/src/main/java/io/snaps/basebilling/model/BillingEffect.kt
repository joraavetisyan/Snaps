package io.snaps.basebilling.model

data class BillingEffect<T>(
    val status: BillingConnectionStatus,
    val data: T?,
)