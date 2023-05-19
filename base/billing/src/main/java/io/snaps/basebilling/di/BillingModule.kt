package io.snaps.basebilling.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basebilling.BillingRouter
import io.snaps.basebilling.PurchaseStateProvider
import io.snaps.basebilling.PurchaseStateProviderImpl
import io.snaps.basebilling.SimpleBilling
import io.snaps.basebilling.SimpleBillingImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // todo UserSessionScoped?
interface BillingModule {

    @Binds
    @Singleton
    fun simpleBilling(billing: SimpleBillingImpl): SimpleBilling

    @Binds
    @Singleton
    fun billingMediator(simpleBilling: SimpleBilling): BillingRouter

    @Binds
    @Singleton
    fun purchaseStateProvider(provider: PurchaseStateProviderImpl): PurchaseStateProvider
}