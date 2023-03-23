package io.snaps.featurewallet.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.model.TransactionType
import io.snaps.basesources.NotificationsSource
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.domain.TotalBalanceModel
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.WalletAddress
import io.snaps.corecommon.model.WalletModel
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.network.Action
import io.snaps.coreui.barcode.BarcodeManager
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.featurewallet.data.TransactionsRepository
import io.snaps.featurewallet.domain.InsufficientBalanceError
import io.snaps.featurewallet.domain.WalletInteractor
import io.snaps.featurewallet.screen.RewardsTileState
import io.snaps.featurewallet.screen.TransactionsUiState
import io.snaps.featurewallet.screen.toTransactionsUiState
import io.snaps.featurewallet.toCellTileStateList
import io.snaps.featurewallet.toRewardsTileState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    mainHeaderHandlerDelegate: MainHeaderHandler,
    private val walletRepository: WalletRepository,
    private val transactionsRepository: TransactionsRepository,
    private val profileRepository: ProfileRepository,
    private val barcodeManager: BarcodeManager,
    private val notificationsSource: NotificationsSource,
    private val action: Action,
    private val walletInteractor: WalletInteractor,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var wallets = listOf<WalletModel>()

    init {
        subscribeToTransactions()
        subscribeToBalance()
        subscribeToRewards()
        subscribeToWallets()
        loadRewards()
    }

    private fun subscribeToWallets() {
        walletRepository.activeWallets.onEach { wallets ->
            this.wallets = wallets
            _uiState.update {
                it.copy(
                    address = wallets.firstOrNull()?.receiveAddress.orEmpty(),
                    wallets = wallets.toCellTileStateList(),
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToBalance() {
        walletRepository.totalBalanceValue.onEach { totalBalance ->
            _uiState.update { it.copy(totalBalance = totalBalance) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToTransactions() {
        transactionsRepository.getTransactionsState().onEach { state ->
            _uiState.update {
                it.copy(
                    transactions = state.toTransactionsUiState(
                        onReloadClicked = ::onTransactionsReloadClicked,
                        onListEndReaching = ::onListEndReaching,
                        onClicked = {},
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToRewards() {
        profileRepository.balanceState.map {
            it.toRewardsTileState(::onRewardReloadClicked)
        }.onEach { rewards ->
            _uiState.update { it.copy(rewards = rewards) }
        }.launchIn(viewModelScope)
    }

    private fun loadRewards() = viewModelScope.launch {
        action.execute { profileRepository.updateBalance() }
    }

    fun onTopUpClicked() {
        showWalletSelectBottomDialog { walletModel ->
            viewModelScope.launch {
                _uiState.update {
                    val qr = barcodeManager.getQrCodeBitmap(walletModel.receiveAddress)
                    it.copy(
                        bottomDialogType = BottomDialogType.TopUp(
                            title = walletModel.symbol,
                            address = walletModel.receiveAddress,
                            qr = qr,
                        )
                    )
                }
                _command publish Command.ShowBottomDialog
            }
        }
    }

    fun onWithdrawClicked() {
        showWalletSelectBottomDialog {
            viewModelScope.launch {
                _command publish Command.OpenWithdrawScreen(it)
            }
        }
    }

    fun onRewardsWithdrawClicked() {
        viewModelScope.launch {
            action.execute {
                walletInteractor.claim()
            }.doOnSuccess {
                profileRepository.updateBalance()
            }.doOnError { error, _ ->
                when (error.cause) {
                    InsufficientBalanceError -> notificationsSource.sendError(
                        StringKey.WalletErrorInsufficientBalance.textValue()
                    )
                }
            }
        }
    }

    fun onDropdownMenuItemClicked(transactionType: TransactionType) {
        _uiState.update {
            it.copy(transactionType = transactionType)
        }
    }

    fun onExchangeClicked() = viewModelScope.launch {
        // todo
    }

    private fun showWalletSelectBottomDialog(onSelected: (WalletModel) -> Unit) =
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    bottomDialogType = BottomDialogType.SelectWallet(
                        wallets = wallets.toCellTileStateList(onSelected),
                    ),
                )
            }
            _command publish Command.ShowBottomDialog
        }

    fun onAddressCopied() {
        viewModelScope.launch {
            notificationsSource.sendMessage(StringKey.WalletMessageAddressCopied.textValue())
        }
    }

    private fun onTransactionsReloadClicked() = viewModelScope.launch {
        action.execute {
            transactionsRepository.refreshTransactions()
        }
    }

    private fun onListEndReaching() = viewModelScope.launch {
        action.execute {
            transactionsRepository.loadNextTransactionsPage()
        }
    }

    private fun onRewardReloadClicked() {
        loadRewards()
    }

    data class UiState(
        val address: String = "",
        val totalBalance: TotalBalanceModel = TotalBalanceModel.empty,
        val wallets: List<CellTileState> = List(3) {
            CellTileState.Shimmer(
                leftPart = LeftPart.Shimmer,
                middlePart = MiddlePart.Shimmer(needValueLine = true, needHeaderLine = true),
                rightPart = RightPart.Shimmer(needRightLine = true),
            )
        },
        val rewards: List<RewardsTileState> = List(2) { RewardsTileState.Shimmer },
        val bottomDialogType: BottomDialogType = BottomDialogType.SelectWallet(),
        val transactions: TransactionsUiState = TransactionsUiState(),
        val transactionType: TransactionType = TransactionType.All,
    )

    sealed class BottomDialogType {

        data class SelectWallet(
            val wallets: List<CellTileState> = emptyList(),
        ) : BottomDialogType()

        data class TopUp(
            val title: String,
            val address: WalletAddress,
            val qr: Bitmap?,
        ) : BottomDialogType()
    }

    sealed class Command {
        data class OpenWithdrawScreen(val wallet: WalletModel) : Command()
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
    }
}