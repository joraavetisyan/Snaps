package io.snaps.featurewallet.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.basesources.NotificationsSource
import io.snaps.basewallet.data.SendHandler
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.WalletModel
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    mainHeaderHandlerDelegate: MainHeaderHandler,
    private val walletRepository: WalletRepository,
    private val action: Action,
    private val notificationsSource: NotificationsSource,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val args = savedStateHandle.requireArgs<AppRoute.Withdraw.Args>()

    private var sendJob: Job? = null
    private var state: SendHandler.State? = null

    private val _uiState = MutableStateFlow(
        UiState(
            walletModel = args.wallet,
            availableAmount = "${
                walletRepository.getAvailableBalance(args.wallet).toString()
            } ${args.wallet.symbol}",
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onAmountValueChanged(amount: String) {
        _uiState.update { it.copy(amountValue = amount) }
    }

    fun onAddressValueChanged(address: String) {
        _uiState.update { it.copy(addressValue = address) }
    }

    fun onMaxButtonClicked() {
        _uiState.update { state ->
            state.copy(amountValue = state.availableAmount.filter { it.isDigit() || it == '.' })
        }
    }

    fun onConfirmTransactionClicked() {
        sendJob?.cancel()
        viewModelScope.launch {
            action.execute {
                walletRepository.send(
                    amount = _uiState.value.amountValue,
                    address = _uiState.value.addressValue,
                    wallet = args.wallet,
                )
            }.doOnSuccess { handler ->
                _uiState.update { it.copy(isLoading = true) }
                sendJob = handler.state.onEach { state ->
                    this@WithdrawViewModel.state = state
                    when (state) {
                        SendHandler.State.Failed -> {
                            notificationsSource.sendError("Error".textValue())
                            _uiState.update { it.copy(isLoading = false) }
                        }
                        SendHandler.State.Idle -> Unit
                        SendHandler.State.Sending -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is SendHandler.State.Ready -> {
                            _uiState.update {
                                it.copy(
                                    transactionFee = state.fee,
                                    totalAmount = state.total,
                                    isSendEnabled = true,
                                    isLoading = false,
                                    onSendClicked = handler::send,
                                )
                            }
                        }
                        SendHandler.State.Sent -> {
                            notificationsSource.sendMessage("Success".textValue())
                            _command publish Command.CloseScreen
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    data class UiState(
        val walletModel: WalletModel,
        val addressValue: String = "",
        val amountValue: String = "",
        val availableAmount: String = "",
        val transactionFee: String = "",
        val totalAmount: String = "",
        val isLoading: Boolean = false,
        val isSendEnabled: Boolean = false,
        val onSendClicked: () -> Unit = {},
    ) {

        val isConfirmEnabled: Boolean get() = addressValue.isNotBlank() && amountValue.isNotBlank()
    }

    sealed interface Command {
        object CloseScreen : Command
    }
}