package io.snaps.featuretasks.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.domain.QuestInfoModel
import io.snaps.baseprofile.domain.QuestModel
import io.snaps.basesession.AppRouteProvider
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesources.BottomDialogBarVisibilityHandler
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.date.toLong
import io.snaps.corecommon.model.OnboardingType
import io.snaps.corecommon.model.State
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featuretasks.data.TasksRepository
import io.snaps.featuretasks.presentation.HistoryTasksUiState
import io.snaps.featuretasks.presentation.energyProgress
import io.snaps.featuretasks.presentation.toHistoryTasksUiState
import io.snaps.featuretasks.presentation.toNftCollectionItemState
import io.snaps.featuretasks.presentation.toRemainingTimeTileState
import io.snaps.featuretasks.presentation.toTaskTileState
import io.snaps.featuretasks.presentation.ui.RemainingTimeTileState
import io.snaps.featuretasks.presentation.ui.TaskTileState
import kotlinx.coroutines.Job
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class TasksViewModel @Inject constructor(
    @Bridged mainHeaderHandler: MainHeaderHandler,
    onboardingHandler: OnboardingHandler,
    bottomDialogBarVisibilityHandler: BottomDialogBarVisibilityHandler,
    private val action: Action,
    private val appRouteProvider: AppRouteProvider,
    @Bridged private val profileRepository: ProfileRepository,
    private val tasksRepository: TasksRepository,
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

    private var roundTimerJob: Job? = null

    init {
        subscribeOnMenuRouteState()
        subscribeToCurrentTasks()
        subscribeToHistoryTasks()
        subscribeToUserNftCollection()
        subscribeToEnergyProgress()
        subscribeToBrokenGlassesCount()

        refreshNfts()

        checkOnboarding(OnboardingType.Tasks)
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
                profileRepository.updateData(isSilently = true)
            }.doOnComplete {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private fun subscribeToCurrentTasks() {
        profileRepository.currentTasksState.onEach { state ->
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
        profileRepository.currentTasksState.combine(flow = nftRepository.allGlassesBrokenState) { tasks, allBroken ->
            tasks.dataOrCache?.totalEnergyProgress.takeIf { allBroken.dataOrCache != true } ?: 0
        }.onEach { state ->
            _uiState.update { it.copy(totalEnergyProgress = state) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToHistoryTasks() {
        tasksRepository.getHistoryTasksState().map { state ->
            _uiState.update {
                it.copy(
                    history = state.toHistoryTasksUiState(
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
            action.execute { tasksRepository.refreshHistoryTasks() }.doOnComplete {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private fun onListEndReaching() = viewModelScope.launch {
        action.execute { tasksRepository.loadNextHistoryTaskPage() }
    }

    private fun onCurrentTaskItemClicked(quest: QuestModel) = viewModelScope.launch {
        _command publish Command.OpenTaskDetailsScreen(
            AppRoute.TaskDetails.Args(
                type = quest.type,
                energy = quest.energy,
                energyProgress = quest.energyProgress(),
                completed = quest.energyProgress() == quest.energy,
            )
        )
    }

    private fun onHistoryTaskItemClicked(task: QuestModel) = viewModelScope.launch {
        onCurrentTaskItemClicked(task)
    }

    private fun State<QuestInfoModel>.startRoundTimer() {
        val endRoundTime = this.dataOrCache?.questDate?.toLong().also {
            if (it == null) {
                _uiState.update {
                    it.copy(remainingTime = toRemainingTimeTileState(0))
                }
            }
        } ?: return
        roundTimerJob?.cancel()
        roundTimerJob = viewModelScope.launch {
            var current = endRoundTime - System.currentTimeMillis()
            while (isActive && current > 0) {
                _uiState.update {
                    it.copy(remainingTime = toRemainingTimeTileState(current))
                }
                delay(1000L)
                current -= 1000L
                if (current <= 0) {
                    onRoundTimerFinished()
                }
            }
        }
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
        val current: List<TaskTileState> = List(6) { TaskTileState.Shimmer },
        val history: HistoryTasksUiState = HistoryTasksUiState(),
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
        data class OpenTaskDetailsScreen(val args: AppRoute.TaskDetails.Args) : Command()
        data class OpenNftDetailsScreen(val args: AppRoute.UserNftDetails.Args) : Command()
    }
}