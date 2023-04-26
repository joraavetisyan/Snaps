package io.snaps.featuretasks.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.domain.QuestInfoModel
import io.snaps.baseprofile.domain.QuestModel
import io.snaps.basesession.AppRouteProvider
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesources.BottomDialogBarVisibilityHandler
import io.snaps.corecommon.date.toLong
import io.snaps.corecommon.model.OnboardingType
import io.snaps.corecommon.model.State
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
    mainHeaderHandler: MainHeaderHandler,
    onboardingHandlerDelegate: OnboardingHandler,
    bottomDialogBarVisibilityHandlerDelegate: BottomDialogBarVisibilityHandler,
    private val action: Action,
    private val appRouteProvider: AppRouteProvider,
    private val profileRepository: ProfileRepository,
    private val tasksRepository: TasksRepository,
    private val nftRepository: NftRepository,
) : SimpleViewModel(),
    MainHeaderHandler by mainHeaderHandler,
    OnboardingHandler by onboardingHandlerDelegate,
    BottomDialogBarVisibilityHandler by bottomDialogBarVisibilityHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private var roundTimerJob: Job? = null

    init {
        subscribeOnMenuRouteState()
        subscribeToCurrentQuests()
        subscribeToHistoryQuests()
        subscribeToUserNftCollection()
        loadUserNftCollection()
        checkOnboarding(OnboardingType.Tasks)
    }

    private fun subscribeToUserNftCollection() {
        nftRepository.nftCollectionState.onEach { state ->
            _uiState.update {
                it.copy(
                    userNftCollection = state.toNftCollectionItemState(
                        onReloadClicked = ::onUserNftReloadClicked,
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeOnMenuRouteState() {
        appRouteProvider.menuRouteState
            .filter { it == AppRoute.MainBottomBar.MainTab3Start.pattern }
            .onEach { loadCurrentTasks() }
            .launchIn(viewModelScope)
    }

    private fun subscribeToCurrentQuests() {
        profileRepository.currentQuestsState.onEach { state ->
            _uiState.update {
                it.copy(
                    current = state.toTaskTileState(
                        onReloadClicked = ::onCurrentReloadClicked,
                        onItemClicked = ::onCurrentTaskItemClicked,
                    ),
                    totalEnergy = state.dataOrCache?.totalEnergy ?: 0,
                    totalEnergyProgress = state.dataOrCache?.quests?.sumOf { quest ->
                        quest.energyProgress()
                    } ?: 0,
                )
            }
            state.startRoundTimer()
        }.launchIn(viewModelScope)
    }

    private fun subscribeToHistoryQuests() {
        tasksRepository.getHistoryTasksState().map { state ->
            _uiState.update {
                it.copy(
                    history = state.toHistoryTasksUiState(
                        shimmerListSize = 6,
                        onReloadClicked = ::onHistoryReloadClicked,
                        onListEndReaching = ::onListEndReaching,
                        onItemClicked = ::onHistoryTaskItemClicked,
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun onHistoryReloadClicked() = viewModelScope.launch {
        action.execute {
            tasksRepository.refreshHistoryTasks()
        }
    }

    private fun loadCurrentTasks() = viewModelScope.launch {
        action.execute {
            profileRepository.updateData()
        }
    }

    private fun loadUserNftCollection() = viewModelScope.launch {
        action.execute {
            nftRepository.updateNftCollection()
        }
    }

    private fun onCurrentReloadClicked() {
        loadCurrentTasks()
    }

    private fun onUserNftReloadClicked() {
        loadUserNftCollection()
    }

    private fun onListEndReaching() = viewModelScope.launch {
        action.execute {
            tasksRepository.loadNextHistoryTaskPage()
        }
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
        loadCurrentTasks()
        loadUserNftCollection()
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

    data class UiState(
        val current: List<TaskTileState> = List(6) { TaskTileState.Shimmer },
        val history: HistoryTasksUiState = HistoryTasksUiState(),
        val remainingTime: RemainingTimeTileState = RemainingTimeTileState.Shimmer,
        val userNftCollection: List<CollectionItemState> = List(6) { CollectionItemState.Shimmer },
        val totalEnergy: Int = 0,
        val totalEnergyProgress: Int = 0,
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
    }
}