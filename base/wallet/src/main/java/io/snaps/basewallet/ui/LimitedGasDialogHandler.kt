package io.snaps.basewallet.ui

import io.snaps.basesources.NotificationsSource
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface LimitedGasDialogHandler {

    val limitedGasState: StateFlow<UiState>

    val limitedGasCommand: Flow<Command>

    fun checkGas(scope: CoroutineScope, minValue: Double, onGasEnough: suspend () -> Unit)

    fun onLimitedGasDialogHidden()

    data class UiState(
        val isLoading: Boolean = false,
        val bottomDialog: BottomDialog? = null,
    )

    sealed class BottomDialog {
        data class Refill(val onRefillClicked: () -> Unit) : BottomDialog()
    }

    sealed class Command {
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
    }
}

class LimitedGasDialogHandlerImplDelegate @Inject constructor(
    private val action: Action,
    private val notificationsSource: NotificationsSource,
    @Bridged private val walletRepository: WalletRepository,
) : LimitedGasDialogHandler {

    private val _uiState = MutableStateFlow(LimitedGasDialogHandler.UiState())
    override val limitedGasState = _uiState.asStateFlow()

    private val _command = Channel<LimitedGasDialogHandler.Command>()
    override val limitedGasCommand = _command.receiveAsFlow()

    override fun onLimitedGasDialogHidden() {
        _uiState.update { it.copy(bottomDialog = null) }
    }

    override fun checkGas(scope: CoroutineScope, minValue: Double, onGasEnough: suspend () -> Unit) {
        scope.launch {
            if (isGasEnough(minValue = minValue)) {
                onGasEnough()
            } else {
                _uiState.update {
                    it.copy(
                        bottomDialog = LimitedGasDialogHandler.BottomDialog.Refill(
                            onRefillClicked = { onRefillClicked(scope = scope, amount = minValue) },
                        )
                    )
                }
                _command publish LimitedGasDialogHandler.Command.ShowBottomDialog
            }
        }
    }

    private fun isGasEnough(minValue: Double): Boolean {
        return walletRepository.bnb.value?.coinValue?.value?.let { it >= minValue } ?: false
    }

    private fun onRefillClicked(scope: CoroutineScope, amount: Double) {
        scope.launch {
            _command publish LimitedGasDialogHandler.Command.HideBottomDialog
            _uiState.update { it.copy(isLoading = true) }
            action.execute {
                walletRepository.refillGas(amount)
            }.doOnSuccess {
                // todo possible inf loop
                while (!isGasEnough(minValue = amount)) {
                    walletRepository.updateTotalBalance()
                    delay(1000L)
                }
                notificationsSource.sendMessage(StringKey.MessageSuccess.textValue())
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}