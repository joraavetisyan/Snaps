package io.snaps.featurewallet.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.model.PaymentsState
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesources.NotificationsSource
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.domain.TotalBalanceModel
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.OnboardingType
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
import io.snaps.featurewallet.data.TransactionsType
import io.snaps.featurewallet.domain.InsufficientBalanceError
import io.snaps.featurewallet.domain.WalletInteractor
import io.snaps.featurewallet.screen.RewardsTileState
import io.snaps.featurewallet.screen.TransactionsUiState
import io.snaps.featurewallet.screen.toTransactionsUiState
import io.snaps.featurewallet.toCellTileStateList
import io.snaps.featurewallet.toRewardsTileState
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    onboardingHandlerDelegate: OnboardingHandler,
    private val action: Action,
    private val barcodeManager: BarcodeManager,
    private val walletInteractor: WalletInteractor,
    private val walletRepository: WalletRepository,
    private val transactionsRepository: TransactionsRepository,
    private val profileRepository: ProfileRepository,
    private val notificationsSource: NotificationsSource,
    private val nftRepository: NftRepository,
) : SimpleViewModel(), OnboardingHandler by onboardingHandlerDelegate {

    private val _uiState = MutableStateFlow(
        UiState(
            isRewardsWithdrawVisible = profileRepository.state.value.dataOrCache?.paymentsState?.let {
                it == PaymentsState.Blockchain
            } ?: false,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var wallets = listOf<WalletModel>()

    init {
        subscribeToTotalBalance()
        subscribeToWallets()
        subscribeToRewards()
        subscribeToUnlockedTransactions()
        subscribeToLockedTransactions()
        subscribeToUserNft()

        updateBalance()

        checkOnboarding(OnboardingType.Wallet)
    }

    private fun subscribeToTotalBalance() {
        walletRepository.totalBalanceValue.onEach { totalBalance ->
            _uiState.update { it.copy(totalBalance = totalBalance) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToWallets() {
        walletRepository.activeWallets.combine(flow = walletInteractor.snpFiatState) { wallets, balance ->
            wallets.map {
                if (it.symbol == "SNAPS") {
                    it.copy(fiatValue = balance.dataOrCache.orEmpty())
                } else {
                    it
                }
            }
        }.onEach { state ->
            this.wallets = state
            _uiState.update {
                it.copy(
                    address = state.firstOrNull()?.receiveAddress.orEmpty(),
                    wallets = state.toCellTileStateList(
                        onClick = ::openWithdrawScreen,
                    ),
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToRewards() {
        profileRepository.balanceState.map { state ->
            _uiState.update {
                it.copy(availableTokens = state.dataOrCache?.unlocked ?: 0.0)
            }
            state.toRewardsTileState(::onRewardReloadClicked)
        }.onEach { rewards ->
            _uiState.update { it.copy(rewards = rewards) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToUnlockedTransactions() {
        transactionsRepository.getTransactionsState(TransactionsType.Unlocked).onEach { state ->
            _uiState.update {
                it.copy(
                    unlockedTransactions = state.toTransactionsUiState(
                        onReloadClicked = ::onUnlockedTransactionsReloadClicked,
                        onListEndReaching = ::onUnlockedTransactionsListEndReaching,
                        onClicked = {},
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToLockedTransactions() {
        transactionsRepository.getTransactionsState(TransactionsType.Locked).onEach { state ->
            _uiState.update {
                it.copy(
                    lockedTransactions = state.toTransactionsUiState(
                        onReloadClicked = ::onLockedTransactionsReloadClicked,
                        onListEndReaching = ::onLockedTransactionsListEndReaching,
                        onClicked = {},
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToUserNft() {
        nftRepository.countBrokenGlassesState.onEach { state ->
            _uiState.update {
                it.copy(
                    countBrokenGlasses = state.dataOrCache ?: 0,
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun updateBalance() = viewModelScope.launch {
        action.execute { profileRepository.updateBalance() }
    }

    fun onRewardsOpened() {
        checkOnboarding(OnboardingType.Rewards)
    }

    fun onRewardsFootnoteClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(bottomDialog = BottomDialog.RewardsFootnote) }
            _command publish Command.ShowBottomDialog
        }
    }

    fun onTopUpClicked() {
        showWalletSelectBottomDialog { walletModel ->
            viewModelScope.launch {
                _uiState.update {
                    val qr = barcodeManager.getQrCodeBitmap(walletModel.receiveAddress)
                    it.copy(
                        bottomDialog = BottomDialog.TopUp(
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
            openWithdrawScreen(it)
        }
    }

    private fun openWithdrawScreen(model: WalletModel) {
        viewModelScope.launch {
            _command publish Command.OpenWithdrawScreen(model)
        }
    }

    fun onRewardsWithdrawClicked() = viewModelScope.launch {
        if (uiState.value.countBrokenGlasses > 0) {
            notificationsSource.sendError(StringKey.RewardsErrorRepairGlasses.textValue())
        } else if (uiState.value.availableTokens == 0.0) {
            notificationsSource.sendError(StringKey.RewardsErrorInsufficientBalance.textValue())
        } else {
            _command publish Command.ShowBottomDialog
            _uiState.update {
                it.copy(
                    amountToClaimValue = "",
                    bottomDialog = BottomDialog.RewardsWithdraw,
                )
            }
        }
    }

    fun onAmountToClaimValueChanged(amount: String) {
        _uiState.update { it.copy(amountToClaimValue = amount) }
    }

    fun onMaxButtonClicked() {
        _uiState.update {
            it.copy(amountToClaimValue = it.availableTokens.toString())
        }
    }

    fun onConfirmClaimClicked() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        action.execute {
            walletInteractor.claim(uiState.value.amountToClaimValue.toDouble())
        }.doOnSuccess {
            profileRepository.updateBalance()
            _command publish Command.HideBottomDialog
        }.doOnError { error, _ ->
            when (error.cause) {
                InsufficientBalanceError -> notificationsSource.sendError(
                    StringKey.RewardsErrorInsufficientBalance.textValue()
                )
            }
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onDropdownMenuItemClicked(filterOptions: FilterOptions) {
        _uiState.update {
            it.copy(filterOptions = filterOptions)
        }
    }

    fun onExchangeClicked() {
        showWalletSelectBottomDialog {
            viewModelScope.launch {
                _command publish Command.OpenExchangeScreen(it)
            }
        }
    }

    fun onPageSelected(index: Int) {
        _uiState.update {
            it.copy(screen = Screen.getByOrdinal(index) ?: Screen.Wallet)
        }
    }

    private fun showWalletSelectBottomDialog(onSelected: (WalletModel) -> Unit) =
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    bottomDialog = BottomDialog.SelectWallet(
                        wallets = wallets.toCellTileStateList(
                            onClick = onSelected,
                        ),
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

    fun refresh() {
        when (uiState.value.screen) {
            Screen.Wallet -> refreshWallet()
            Screen.Rewards -> refreshRewards()
        }
    }

    private fun refreshWallet() = viewModelScope.launch {
        _uiState.update { it.copy(isRefreshing = true) }
        action.execute {
            val loadBalanceDeferred = viewModelScope.async { walletRepository.updateBalance() }

            loadBalanceDeferred.await()
        }.doOnComplete {
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun refreshRewards() = viewModelScope.launch {
        _uiState.update { it.copy(isRefreshing = true) }
        action.execute {
            val loadBalanceDeferred = viewModelScope.async { profileRepository.updateBalance() }
            val loadUnlockedTransactionsDeferred = viewModelScope.async {
                transactionsRepository.refreshTransactions(TransactionsType.Unlocked)
            }
            val loadLockedTransactionsDeferred = viewModelScope.async {
                transactionsRepository.refreshTransactions(TransactionsType.Locked)
            }

            loadBalanceDeferred.await()
            loadLockedTransactionsDeferred.await()
            loadUnlockedTransactionsDeferred.await()
        }.doOnComplete {
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun onUnlockedTransactionsReloadClicked() = viewModelScope.launch {
        action.execute {
            transactionsRepository.refreshTransactions(TransactionsType.Unlocked)
        }
    }

    private fun onUnlockedTransactionsListEndReaching() = viewModelScope.launch {
        action.execute {
            transactionsRepository.loadNextTransactionsPage(TransactionsType.Unlocked)
        }
    }

    private fun onLockedTransactionsReloadClicked() = viewModelScope.launch {
        action.execute {
            transactionsRepository.refreshTransactions(TransactionsType.Locked)
        }
    }

    private fun onLockedTransactionsListEndReaching() = viewModelScope.launch {
        action.execute {
            transactionsRepository.loadNextTransactionsPage(TransactionsType.Locked)
        }
    }

    private fun onRewardReloadClicked() {
        updateBalance()
    }

    data class UiState(
        val address: String = "",
        val totalBalance: TotalBalanceModel = TotalBalanceModel.empty,
        val wallets: List<CellTileState> = List(3) {
            CellTileState.Shimmer(
                leftPart = LeftPart.Shimmer,
                middlePart = MiddlePart.Shimmer(needValueLine = true, needHeaderLine = true),
                rightPart = RightPart.Shimmer(needLine = true),
            )
        },
        val rewards: List<RewardsTileState> = List(2) { RewardsTileState.Shimmer },
        val bottomDialog: BottomDialog = BottomDialog.SelectWallet(),
        val unlockedTransactions: TransactionsUiState = TransactionsUiState(),
        val lockedTransactions: TransactionsUiState = TransactionsUiState(),
        val filterOptions: FilterOptions = FilterOptions.Unlocked,
        val isRewardsWithdrawVisible: Boolean = false,
        val countBrokenGlasses: Int = 0,
        val isRefreshing: Boolean = false,
        val screen: Screen = Screen.Wallet,
        val amountToClaimValue: String = "",
        val availableTokens: Double = 0.0,
        val isLoading: Boolean = false,
    ) {

        val isConfirmClaimEnabled get() = amountToClaimValue.toDoubleOrNull()?.let {
            it <= availableTokens && it > 0
        } ?: false
    }

    sealed class BottomDialog {

        data class SelectWallet(
            val wallets: List<CellTileState> = emptyList(),
        ) : BottomDialog()

        data class TopUp(
            val title: String,
            val address: WalletAddress,
            val qr: Bitmap?,
        ) : BottomDialog()

        object RewardsFootnote : BottomDialog()

        object RewardsWithdraw : BottomDialog()
    }

    enum class FilterOptions {
        Unlocked, Locked
    }

    enum class Screen(val label: TextValue) {
        Wallet(StringKey.WalletTitle.textValue()),
        Rewards(StringKey.RewardsTitle.textValue());

        companion object {
            fun getByOrdinal(ordinal: Int) = values().firstOrNull { it.ordinal == ordinal }
        }
    }

    sealed class Command {
        data class OpenWithdrawScreen(val wallet: WalletModel) : Command()
        data class OpenExchangeScreen(val wallet: WalletModel) : Command()
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
    }
}