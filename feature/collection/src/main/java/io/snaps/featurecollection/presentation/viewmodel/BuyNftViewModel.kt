package io.snaps.featurecollection.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basebilling.SimpleBilling
import io.snaps.basebilling.model.Product
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyNftViewModel @Inject constructor(
    private val action: Action,
    billing: SimpleBilling
) : SimpleViewModel(), SimpleBilling by billing {

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onBuyClicked() = viewModelScope.launch {
        val products = getInAppProducts().data.orEmpty()
        products.firstOrNull()?.let {
            _command publish Command.OpenBillingScreen(it)
        }
    }

    sealed class Command {
        data class OpenBillingScreen(val product: Product) : Command()
    }
}