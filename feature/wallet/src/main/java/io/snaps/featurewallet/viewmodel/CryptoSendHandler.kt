package io.snaps.featurewallet.viewmodel

import io.snaps.basesources.NotificationsSource
import io.snaps.basewallet.data.SendHandler
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.WalletModel
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface CryptoSendHandler {

    val cryptoSendState: StateFlow<UiState>

    val cryptoSendCommand: Flow<Command>

    fun onSendClicked(
        scope: CoroutineScope,
        wallet: WalletModel,
        address: String,
        amount: String,
        sendImmediately: Boolean = false,
    )

    data class UiState(
        val transactionFee: String = "",
        val totalAmount: String = "",
        val isLoading: Boolean = false,
        val isSendEnabled: Boolean = false,
        val onSendClicked: () -> Unit = {},
    )

    sealed interface Command {
        object CloseScreen : Command
    }
}

class CryptoSendHandlerImplDelegate @Inject constructor(
    private val action: Action,
    private val walletRepository: WalletRepository,
    private val notificationsSource: NotificationsSource,
) : CryptoSendHandler {

    private val _uiState = MutableStateFlow(CryptoSendHandler.UiState())
    override val cryptoSendState = _uiState.asStateFlow()

    private val _command = Channel<CryptoSendHandler.Command>()
    override val cryptoSendCommand = _command.receiveAsFlow()

    private var sendJob: Job? = null
    private var state: SendHandler.State? = null

    override fun onSendClicked(
        scope: CoroutineScope,
        wallet: WalletModel,
        address: String,
        amount: String,
        sendImmediately: Boolean,
    ) {
        sendJob?.cancel()
        scope.launch {
            action.execute {
                walletRepository.send(
                    amount = amount,
                    address = address,
                    wallet = wallet,
                )
            }.doOnSuccess { handler ->
                _uiState.update { it.copy(isLoading = true) }
                sendJob = handler.state.onEach { state ->
                    this@CryptoSendHandlerImplDelegate.state = state
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
                            _command publish CryptoSendHandler.Command.CloseScreen
                        }
                    }
                    if (sendImmediately) {
                        handler.send()
                    }
                }.launchIn(scope)
            }
        }
    }
}