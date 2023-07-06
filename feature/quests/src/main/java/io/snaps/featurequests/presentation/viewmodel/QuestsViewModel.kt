package io.snaps.featurequests.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basequests.data.QuestsRepository
import io.snaps.basequests.data.model.SocialPostStatus
import io.snaps.basequests.domain.QuestInfoModel
import io.snaps.basequests.domain.QuestModel
import io.snaps.basesession.AppRouteProvider
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesources.BottomDialogBarVisibilityHandler
import io.snaps.basesources.NotificationsSource
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.date.CountdownTimer
import io.snaps.corecommon.model.OnboardingType
import io.snaps.corecommon.model.QuestType
import io.snaps.corecommon.model.State
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featurequests.presentation.HistoryQuestsUiState
import io.snaps.featurequests.presentation.energyProgress
import io.snaps.featurequests.presentation.toHistoryQuestsUiState
import io.snaps.featurequests.presentation.toNftCollectionItemState
import io.snaps.featurequests.presentation.toRemainingTimeTileState
import io.snaps.featurequests.presentation.toTaskTileState
import io.snaps.featurequests.presentation.ui.QuestTileState
import io.snaps.featurequests.presentation.ui.RemainingTimeTileState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class QuestsViewModel @Inject constructor(
    @Bridged mainHeaderHandler: MainHeaderHandler,
    @Bridged onboardingHandler: OnboardingHandler,
    bottomDialogBarVisibilityHandler: BottomDialogBarVisibilityHandler,
    private val action: Action,
    private val appRouteProvider: AppRouteProvider,
    private val notificationsSource: NotificationsSource,
    @Bridged private val questsRepository: QuestsRepository,
    @Bridged private val profileRepository: ProfileRepository,
    @Bridged private val nftRepository: NftRepository,
    @Bridged private val walletRepository: WalletRepository,
) : SimpleViewModel(),
    MainHeaderHandler by mainHeaderHandler,
    OnboardingHandler by onboardingHandler,
    BottomDialogBarVisibilityHandler by bottomDialogBarVisibilityHandler {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private val roundTimer = CountdownTimer()

    init {
        subscribeOnMenuRouteState()
        subscribeToCurrentTasks()
        subscribeToHistoryTasks()
        subscribeToUserNftCollection()
        subscribeToEnergyProgress()
        subscribeToBrokenGlassesCount()

        refreshNfts()

        viewModelScope.launch {
            checkOnboarding(OnboardingType.Tasks)
        }
    }

    private fun subscribeToUserNftCollection() {
        nftRepository.nftCollectionState.combine(walletRepository.snpsAccountState) { collection, account ->
            collection.toNftCollectionItemState(
                snpsUsdExchangeRate = account.dataOrCache?.snpsUsdExchangeRate ?: 0.0,
                onReloadClicked = ::refreshNfts,
                onItemClicked = ::onItemClicked,
            )
        }.onEach { state ->
            _uiState.update { it.copy(userNftCollection = state) }
        }.launchIn(viewModelScope)
    }

    private fun refreshNfts() {
        viewModelScope.launch {
            action.execute { nftRepository.updateNftCollection() }.doOnComplete {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private fun subscribeOnMenuRouteState() {
        appRouteProvider.menuRouteState
            .filter { it == AppRoute.MainBottomBar.MainTab3Start.pattern }
            .onEach { refreshCurrentTasks() }
            .launchIn(viewModelScope)
    }

    private fun refreshCurrentTasks() {
        viewModelScope.launch {
            action.execute {
                questsRepository.updateData(isSilently = true)
            }.doOnComplete {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private fun subscribeToCurrentTasks() {
        questsRepository.currentQuestsState.onEach { state ->
            _uiState.update {
                it.copy(
                    current = state.toTaskTileState(
                        onReloadClicked = ::refreshCurrentTasks,
                        onItemClicked = ::onCurrentTaskItemClicked,
                    ),
                    totalEnergy = state.dataOrCache?.totalEnergy ?: 0,
                )
            }
            state.startRoundTimer()
        }.launchIn(viewModelScope)
    }

    private fun subscribeToBrokenGlassesCount() {
        nftRepository.allGlassesBrokenState.onEach { state ->
            _uiState.update { it.copy(isAllGlassesBroken = state.dataOrCache ?: false) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToEnergyProgress() {
        questsRepository.currentQuestsState.combine(flow = nftRepository.allGlassesBrokenState) { tasks, allBroken ->
            tasks.dataOrCache?.totalEnergyProgress.takeIf { allBroken.dataOrCache != true } ?: 0
        }.onEach { state ->
            _uiState.update { it.copy(totalEnergyProgress = state) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToHistoryTasks() {
        questsRepository.getHistoryQuestsState().map { state ->
            _uiState.update {
                it.copy(
                    history = state.toHistoryQuestsUiState(
                        shimmerListSize = 6,
                        onReloadClicked = ::refreshHistory,
                        onListEndReaching = ::onListEndReaching,
                        onItemClicked = ::onHistoryTaskItemClicked,
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun refreshHistory() {
        viewModelScope.launch {
            action.execute { questsRepository.refreshHistoryQuests() }.doOnComplete {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private fun onListEndReaching() = viewModelScope.launch {
        action.execute { questsRepository.loadNextHistoryQuestPage() }
    }

    private fun onCurrentTaskItemClicked(quest: QuestInfoModel) {
        viewModelScope.launch {
            if (quest.type == QuestType.SocialPost && quest.status == SocialPostStatus.WaitForVerification) {
                notificationsSource.sendMessage(StringKey.TaskShareMessagePostInstagram.textValue())
                return@launch
            }
            _command publish Command.OpenQuestDetailsScreen(
                AppRoute.QuestDetails.Args(
                    type = quest.type,
                    energy = quest.energy,
                    count = quest.count,
                    energyProgress = quest.energyProgress(),
                    completed = quest.energyProgress() == quest.energy,
                )
            )
        }
    }

    private fun onHistoryTaskItemClicked(task: QuestInfoModel) = viewModelScope.launch {
        onCurrentTaskItemClicked(task)
    }

    private fun State<QuestModel>.startRoundTimer() {
        val roundEndTime = this.dataOrCache?.questDate.also {
            if (it == null) {
                _uiState.update {
                    it.copy(remainingTime = toRemainingTimeTileState(0.seconds))
                }
            }
        } ?: return
        roundTimer.start(
            scope = viewModelScope,
            tickUntil = roundEndTime,
            onTick = { timeLeft ->
                _uiState.update {
                    it.copy(remainingTime = toRemainingTimeTileState(timeLeft))
                }
            },
            onFinished = ::onRoundTimerFinished,
        )
    }

    private fun onRoundTimerFinished() = viewModelScope.launch {
        delay(10.seconds) // to restart the round
        refreshCurrentTasks()
        refreshNfts()
    }

    private fun onItemClicked(nftModel: NftModel) {
        viewModelScope.launch {
            _command publish Command.OpenNftDetailsScreen(
                args = AppRoute.UserNftDetails.Args(nftId = nftModel.id)
            )
        }
    }

    fun onCurrentTasksFootnoteClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(bottomDialog = BottomDialog.CurrentTasksFootnote) }
            _command publish Command.ShowBottomDialog
        }
    }

    fun onHistoryTasksFootnoteClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(bottomDialog = BottomDialog.HistoryTasksFootnote) }
            _command publish Command.ShowBottomDialog
        }
    }

    fun onFootnoteStartClicked() {
        viewModelScope.launch {
            _command publish Command.HideBottomDialog
        }
    }

    fun onRefreshPulled(currentPage: Int) {
        _uiState.update { it.copy(isRefreshing = true) }
        when (currentPage) {
            0 -> {
                refreshCurrentTasks()
                refreshNfts()
            }
            1 -> refreshHistory()
        }
    }

    data class UiState(
        val isRefreshing: Boolean = false,
        val current: List<QuestTileState> = List(6) { QuestTileState.Shimmer },
        val history: HistoryQuestsUiState = HistoryQuestsUiState(),
        val remainingTime: RemainingTimeTileState = RemainingTimeTileState.Shimmer,
        val userNftCollection: List<CollectionItemState> = List(6) { CollectionItemState.Shimmer },
        val totalEnergy: Int = 0,
        val totalEnergyProgress: Int = 0,
        val isAllGlassesBroken: Boolean = false,
        val bottomDialog: BottomDialog = BottomDialog.CurrentTasksFootnote,
    )

    enum class BottomDialog {
        CurrentTasksFootnote,
        HistoryTasksFootnote,
    }

    sealed class Command {
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
        data class OpenQuestDetailsScreen(val args: AppRoute.QuestDetails.Args) : Command()
        data class OpenNftDetailsScreen(val args: AppRoute.UserNftDetails.Args) : Command()
    }
}