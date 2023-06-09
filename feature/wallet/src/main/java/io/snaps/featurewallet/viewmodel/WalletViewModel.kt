package io.snaps.featurewallet.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.model.PaymentsState
import io.snaps.basesources.remotedata.model.SocialPageType
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesources.NotificationsSource
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.basesources.remotedata.SettingsRepository
import io.snaps.basewallet.data.WalletRepository
import io.snaps.basewallet.domain.TotalBalanceModel
import io.snaps.basewallet.domain.WalletModel
import io.snaps.basewallet.ui.TransferTokensDialogHandler
import io.snaps.basewallet.ui.TransferTokensSuccessData
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.stringAmountToDoubleOrZero
import io.snaps.corecommon.ext.stripTrailingZeros
import io.snaps.corecommon.model.CoinSNPS
import io.snaps.corecommon.model.CoinType
import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.OnboardingType
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
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
import io.snaps.featurewallet.screen.PayoutStatusState
import io.snaps.featurewallet.screen.RewardsTileState
import io.snaps.featurewallet.screen.TransactionsUiState
import io.snaps.featurewallet.screen.toTransactionsUiState
import io.snaps.featurewallet.toCellTileStateList
import io.snaps.featurewallet.toPayoutStatusState
import io.snaps.featurewallet.toRewardsTileState
import kotlinx.coroutines.async
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
    @Bridged onboardingHandler: OnboardingHandler,
    transferTokensDialogHandler: TransferTokensDialogHandler,
    featureToggle: FeatureToggle,
    private val action: Action,
    private val barcodeManager: BarcodeManager,
    private val notificationsSource: NotificationsSource,
    private val settingsRepository: SettingsRepository,
    private val walletInteractor: WalletInteractor,
    @Bridged private val walletRepository: WalletRepository,
    private val transactionsRepository: TransactionsRepository,
    @Bridged private val profileRepository: ProfileRepository,
    @Bridged private val nftRepository: NftRepository,
) : SimpleViewModel(),
    TransferTokensDialogHandler by transferTokensDialogHandler,
    OnboardingHandler by onboardingHandler {

    private val _uiState = MutableStateFlow(UiState(isSnapsSellEnabled = featureToggle.isEnabled(Feature.SellSnaps)))
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var isSubscribedToRewardsData = false
    private var brokenNftCount: Int = 0
    private var wallets = listOf<WalletModel>()

    init {
        subscribeToTotalBalance()
        subscribeToWallets()
        subscribeToUserNft()
        subscribeToPayouts()

        updatePayouts(isSilently = true)

        checkOnboarding(OnboardingType.Wallet)
    }

    private fun subscribeToTotalBalance() {
        walletRepository.totalBalance.onEach { totalBalance ->
            _uiState.update { it.copy(totalBalance = totalBalance) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToWallets() {
        walletRepository.activeWallets.onEach { wallets ->
            this.wallets = wallets
            _uiState.update {
                it.copy(
                    address = wallets.firstOrNull()?.receiveAddress.orEmpty(),
                    wallets = wallets.toCellTileStateList(onClick = ::openWithdrawScreen),
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToUserNft() {
        nftRepository.countBrokenGlassesState.onEach { state ->
            brokenNftCount = state.dataOrCache ?: 0
        }.launchIn(viewModelScope)
    }

    private fun subscribeToPayouts() {
        walletRepository.payouts.map { state ->
            state.toPayoutStatusState(
                onContactSupportClick = ::openSupport,
                onCopyClick = { copyToClipboard(text = it, message = StringKey.MessageCopySuccess.textValue()) },
            )
        }.onEach { state ->
            _uiState.update { it.copy(payoutStatusState = state) }
        }.launchIn(viewModelScope)
    }

    private fun copyToClipboard(text: String, message: TextValue) {
        viewModelScope.launch {
            _command publish Command.CopyText(text)
            notificationsSource.sendMessage(message)
        }
    }

    private fun openSupport() {
        viewModelScope.launch {
            val pages = settingsRepository.state.value.dataOrCache?.socialPages ?: emptyList()
            pages.find { it.type == SocialPageType.Support }?.link?.let {
                _command publish Command.OpenLink(it)
            }
        }
    }

    private fun updatePayouts(isSilently: Boolean) {
        viewModelScope.launch {
            action.execute { walletRepository.updatePayouts(isSilently = isSilently) }
        }
    }

    fun onRewardsOpened() {
        if (!isSubscribedToRewardsData) {
            isSubscribedToRewardsData = true
            subscribeToRewards()
            subscribeToUnlockedTransactions()
            subscribeToLockedTransactions()

            updateBalance()
        }
        checkOnboarding(OnboardingType.Rewards)
    }

    private fun subscribeToRewards() {
        walletRepository.snpsAccountState.map { state ->
            _uiState.update {
                it.copy(availableTokens = state.dataOrCache?.unlocked ?: CoinSNPS(0.0))
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

    private fun updateBalance() = viewModelScope.launch {
        action.execute { walletRepository.updateSnpsAccount() }
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
                            title = walletModel.coinType.symbol,
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
            _command publish Command.OpenWithdrawScreen(model.coinType)
        }
    }

    fun onRewardsClaimClicked() {
        viewModelScope.launch {
            if (profileRepository.state.value.dataOrCache?.paymentsState == PaymentsState.InApp && brokenNftCount > 0) {
                _uiState.update { it.copy(bottomDialog = BottomDialog.RepairNft) }
                _command publish Command.ShowBottomDialog
            } else if (uiState.value.availableTokens.value == 0.0) {
                notificationsSource.sendError(StringKey.RewardsErrorInsufficientBalance.textValue())
            } else {
                _uiState.update { it.copy(claimAmountValue = "", bottomDialog = BottomDialog.RewardsWithdraw) }
                _command publish Command.ShowBottomDialog
            }
        }
    }

    fun onAmountToClaimValueChanged(amount: String) {
        _uiState.update { it.copy(claimAmountValue = amount) }
    }

    fun onRewardsMaxButtonClicked() {
        onAmountToClaimValueChanged(_uiState.value.availableTokens.value.stripTrailingZeros())
    }

    fun onConfirmClaimClicked() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        action.execute {
            walletInteractor.claim(amount = uiState.value.claimAmountValue.toDouble())
                .doOnSuccess {
                    notificationsSource.sendMessage(StringKey.MessageSuccess.textValue())
                    walletRepository.updateSnpsAccount()
                    walletRepository.updateTotalBalance()
                }
        }.doOnError { error, _ ->
            when (error.cause) {
                InsufficientBalanceError -> notificationsSource.sendError(
                    StringKey.RewardsErrorInsufficientBalance.textValue(),
                )
            }
        }.doOnComplete {
            _command publish Command.HideBottomDialog
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onDropdownMenuItemClicked(filter: Filter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun onExchangeClicked() {
        showWalletSelectBottomDialog {
            viewModelScope.launch {
                _command publish Command.OpenExchangeScreen(it.coinType)
            }
        }
    }

    fun onPageSelected(index: Int) {
        _uiState.update { it.copy(screen = Screen.byOrdinal(index)) }
    }

    private fun showWalletSelectBottomDialog(onSelected: (WalletModel) -> Unit) =
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    bottomDialog = BottomDialog.SelectWallet(
                        wallets = wallets.toCellTileStateList(
                            onClick = {
                                onBottomDialogHidden()
                                viewModelScope.launch { _command publish Command.HideBottomDialog }
                                onSelected(it)
                            },
                        ),
                    ),
                )
            }
            _command publish Command.ShowBottomDialog
        }

    // todo copy handler as delegate
    fun onAddressCopyClicked(address: CryptoAddress) {
        copyToClipboard(text = address, message = StringKey.WalletMessageAddressCopied.textValue())
    }

    fun onRefreshPulled() {
        when (uiState.value.screen) {
            Screen.Wallet -> refreshWallet()
            Screen.Rewards -> refreshRewards()
        }
    }

    private fun refreshWallet() = viewModelScope.launch {
        _uiState.update { it.copy(isRefreshing = true) }
        action.execute {
            walletRepository.updateTotalBalanceAndPayouts(isSilently = true)
        }.doOnComplete {
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun refreshRewards() = viewModelScope.launch {
        _uiState.update { it.copy(isRefreshing = true) }
        action.execute {
            val loadBalanceDeferred = viewModelScope.async { walletRepository.updateSnpsAccount() }
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

    fun onSellSnapsClicked() {
        viewModelScope.launch { _command publish Command.OpenWithdrawSnapsScreen }
    }

    fun onTransactionResultReceived(result: TransferTokensSuccessData?) {
        when (result?.type) {
            TransferTokensSuccessData.Type.Send -> {
                updateTotalBalance()
                onSuccessfulTransfer(scope = viewModelScope, data = result)
            }
            TransferTokensSuccessData.Type.Sell -> {
                updateTotalBalance()
                updatePayouts(isSilently = false)
                onSuccessfulSell(scope = viewModelScope, data = result)
            }
            TransferTokensSuccessData.Type.Purchase,
            null -> Unit
        }
    }

    private fun updateTotalBalance() {
        viewModelScope.launch { action.execute { walletRepository.updateTotalBalance() } }
    }

    fun onBottomDialogHidden() {
        _uiState.update { it.copy(bottomDialog = null) }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val screen: Screen = Screen.Wallet,
        val bottomDialog: BottomDialog? = null,

        val payoutStatusState: PayoutStatusState? = null,
        val address: CryptoAddress = "",
        val totalBalance: TotalBalanceModel = TotalBalanceModel.empty,
        val isSnapsSellEnabled: Boolean,
        val wallets: List<CellTileState> = List(3) {
            CellTileState.Shimmer(
                leftPart = LeftPart.Shimmer,
                middlePart = MiddlePart.Shimmer(needValueLine = true, needHeaderLine = true),
                rightPart = RightPart.Shimmer(needLine = true),
            )
        },

        val rewards: List<RewardsTileState> = List(2) { RewardsTileState.Shimmer },
        val filter: Filter = Filter.Unlocked,
        val unlockedTransactions: TransactionsUiState = TransactionsUiState(),
        val lockedTransactions: TransactionsUiState = TransactionsUiState(),
        val availableTokens: CoinValue = CoinSNPS(0.0),
        val claimAmountValue: String = "",
    ) {

        val isConfirmClaimEnabled
            get() = claimAmountValue.stringAmountToDoubleOrZero().let { it > 0 && it <= availableTokens.value }

        val transactions: TransactionsUiState
            get() = when (filter) {
                Filter.Unlocked -> unlockedTransactions
                Filter.Locked -> lockedTransactions
            }
    }

    sealed class BottomDialog {

        data class SelectWallet(
            val wallets: List<CellTileState> = emptyList(),
        ) : BottomDialog()

        data class TopUp(
            val title: String,
            val address: CryptoAddress,
            val qr: Bitmap?,
        ) : BottomDialog()

        object RewardsFootnote : BottomDialog()

        object RepairNft : BottomDialog()

        object RewardsWithdraw : BottomDialog()
    }

    enum class Filter(val label: TextValue) {
        Unlocked(StringKey.RewardsFieldFilterUnlocked.textValue()),
        Locked(StringKey.RewardsFieldFilterLocked.textValue()),
        ;
    }

    enum class Screen(val label: TextValue) {
        Wallet(StringKey.WalletTitle.textValue()),
        Rewards(StringKey.RewardsTitle.textValue()),
        ;

        companion object {

            fun byOrdinal(ordinal: Int) = values().first { it.ordinal == ordinal }
        }
    }

    sealed class Command {
        data class OpenWithdrawScreen(val coinType: CoinType) : Command()
        object OpenWithdrawSnapsScreen : Command()
        data class OpenExchangeScreen(val coinType: CoinType) : Command()
        data class OpenLink(val link: FullUrl) : Command()
        data class CopyText(val text: String) : Command()
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
    }
}