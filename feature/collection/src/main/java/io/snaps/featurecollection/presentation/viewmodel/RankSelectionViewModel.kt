package io.snaps.featurecollection.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.domain.BundleModel
import io.snaps.basenft.domain.MysteryBoxModel
import io.snaps.basenft.domain.RankModel
import io.snaps.basesettings.data.SettingsRepository
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.basewallet.data.WalletRepository
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featurecollection.presentation.screen.BundleTileState
import io.snaps.featurecollection.presentation.screen.MysteryBoxTileState
import io.snaps.featurecollection.presentation.screen.RankTileState
import io.snaps.featurecollection.presentation.toBundleTileState
import io.snaps.featurecollection.presentation.toMysteryBoxTileState
import io.snaps.featurecollection.presentation.toRankTileState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankSelectionViewModel @Inject constructor(
    private val action: Action,
    private val featureToggle: FeatureToggle,
    @Bridged private val nftRepository: NftRepository,
    @Bridged private val walletRepository: WalletRepository,
    private val settingsRepository: SettingsRepository,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(
            isMysteryBoxEnabled = featureToggle.isEnabled(Feature.MysteryBox),
            isBundleEnabled = featureToggle.isEnabled(Feature.Bundle),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        viewModelScope.launch {
            if (settingsRepository.state.value.dataOrCache == null) {
                settingsRepository.update().doOnSuccess {
                    _uiState.update {
                        it.copy(
                            isMysteryBoxEnabled = featureToggle.isEnabled(Feature.MysteryBox),
                            isBundleEnabled = featureToggle.isEnabled(Feature.Bundle),
                        )
                    }
                }
            }

            subscribeOnRanks()
            subscribeOnMysteryBoxes()
            subscribeOnBundles()

            loadRanks()
            loadMysteryBoxes()
            loadBundles()
        }
    }

    private fun subscribeOnRanks() {
        nftRepository.ranksState.combine(walletRepository.snpsAccountState) { ranks, account ->
            ranks.toRankTileState(
                snpsUsdExchangeRate = account.dataOrCache?.snpsUsdExchangeRate ?: 0.0,
                onItemClicked = ::onRankItemClicked,
                onReloadClicked = ::onRanksReloadClicked,
            )
        }.onEach { state ->
            _uiState.update { it.copy(ranks = state) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeOnMysteryBoxes() {
        nftRepository.mysteryBoxState.onEach { state ->
            _uiState.update {
                it.copy(
                    mysteryBoxes = state.toMysteryBoxTileState(
                        onItemClicked = ::onMysteryBoxItemClicked,
                        onReloadClicked = ::onMysteryBoxReloadClicked,
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeOnBundles() {
        nftRepository.bundleState.onEach { state ->
            _uiState.update {
                it.copy(
                    bundles = state.toBundleTileState(
                        onItemClicked = ::onBundleItemClicked,
                        onReloadClicked = ::onBundleReloadClicked,
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun loadRanks() = viewModelScope.launch {
        action.execute {
            nftRepository.updateRanks()
        }
    }

    private fun loadMysteryBoxes() {
        viewModelScope.launch {
            action.execute {
                nftRepository.updateMysteryBoxes()
            }
        }
    }

    private fun loadBundles() {
        viewModelScope.launch {
            action.execute {
                nftRepository.updateBundle()
            }
        }
    }

    private fun onRanksReloadClicked() {
        loadRanks()
    }

    private fun onRankItemClicked(rank: RankModel) = viewModelScope.launch {
        _command publish Command.OpenPurchase(args = AppRoute.Purchase.Args(type = rank.type))
    }

    private fun onMysteryBoxItemClicked(mysteryBox: MysteryBoxModel) {
        viewModelScope.launch {
            _command publish Command.OpenMysteryBox(args = AppRoute.MysteryBox.Args(type = mysteryBox.type))
        }
    }

    private fun onMysteryBoxReloadClicked() {
        loadMysteryBoxes()
    }

    private fun onBundleItemClicked(bundle: BundleModel) {
        viewModelScope.launch {
            _command publish Command.OpenBundle(AppRoute.Bundle.Args(bundle.type))
        }
    }

    private fun onBundleReloadClicked() {
        loadBundles()
    }

    fun onRankFootnoteClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(bottomDialog = BottomDialog.RankFootnote) }
            _command publish Command.ShowBottomDialog
        }
    }

    fun onRaiseNftRankClick() {
        viewModelScope.launch {
            _command publish Command.HideBottomDialog
        }
    }

    data class UiState(
        val ranks: List<RankTileState> = List(6) { RankTileState.Shimmer },
        val mysteryBoxes: List<MysteryBoxTileState> = List(2) { MysteryBoxTileState.Shimmer },
        val bundles: List<BundleTileState> = List(8) { BundleTileState.Shimmer },
        val bottomDialog: BottomDialog = BottomDialog.RankFootnote,
        val isMysteryBoxEnabled: Boolean = false,
        val isBundleEnabled: Boolean = false,
    )

    sealed class BottomDialog {
        object RankFootnote : BottomDialog()
    }

    sealed class Command {
        data class OpenPurchase(val args: AppRoute.Purchase.Args) : Command()
        data class OpenMysteryBox(val args: AppRoute.MysteryBox.Args) : Command()
        data class OpenBundle(val args: AppRoute.Bundle.Args) : Command()
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
    }
}