package io.snaps.basebilling

import com.android.billingclient.api.Purchase
import io.snaps.basebilling.model.BillingEffect
import io.snaps.basebilling.model.Product
import javax.inject.Inject
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface PurchaseStateProvider {

    val isPayedVersionFlow: StateFlow<Boolean>

    val newPurchasesFlow: SharedFlow<List<Purchase>>

    suspend fun getInAppProducts(): BillingEffect<List<Product>>
    suspend fun getSubscribeProducts(): BillingEffect<List<Product>>
    suspend fun getInAppPurchases(): BillingEffect<List<Purchase>>
    suspend fun getSubscribePurchases(): BillingEffect<List<Purchase>>
}

class PurchaseStateProviderImpl @Inject constructor(
    private val billingWrapper: SimpleBilling,
) : PurchaseStateProvider {

    /*
    * stream of new purchases
    * */
    override val newPurchasesFlow = billingWrapper.newPurchasesFlow

    /*
    * paid app status
    * */
    override val isPayedVersionFlow = billingWrapper.isPaidVersionFlow

    override suspend fun getInAppProducts() = billingWrapper.getInAppProducts()

    override suspend fun getSubscribeProducts() = billingWrapper.getSubscribeProducts()

    override suspend fun getInAppPurchases() = billingWrapper.getInAppPurchases()

    override suspend fun getSubscribePurchases() = billingWrapper.getSubscribePurchases()
}