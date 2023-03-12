package io.snaps.featurecollection.presentation.viewmodel

import android.app.Activity
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basebilling.BillingRouter
import io.snaps.basebilling.PurchaseStateProvider
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyNftViewModel @Inject constructor(
    private val action: Action,
    private val purchaseStateProvider: PurchaseStateProvider,
    private val billingRouter: BillingRouter,
) : SimpleViewModel() {

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onBuyClicked(activity: Activity) = viewModelScope.launch {
        val products = purchaseStateProvider.getInAppProducts().data.orEmpty()
        products.firstOrNull()?.let {
            billingRouter.openBillingScreen(it, activity)
        }
    }

    sealed class Command
}