package io.snaps.featureinitialization.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesettings.data.SettingsRepository
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featureinitialization.presentation.screen.InterestsSectorTileState
import io.snaps.featureinitialization.presentation.toInterestsSectorTileState
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
class InterestsSelectionViewModel @Inject constructor(
    private val action: Action,
    private val settingsRepository: SettingsRepository,
    @Bridged private val profileRepository: ProfileRepository,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private val selectedIds = mutableListOf<Uuid>()

    init {
        subscribeOnInterests()

        loadInterests()
    }

    private fun subscribeOnInterests() {
        settingsRepository.interestsState.onEach { state ->
            _uiState.update {
                it.copy(
                    interests = state.toInterestsSectorTileState(
                        selectedIds = selectedIds.toList(),
                        onReloadClick = ::loadInterests,
                        onItemClick = ::onItemClicked,
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onSkipButtonClicked() {
        val interestsSelector = uiState.value.interests
        if (interestsSelector !is InterestsSectorTileState.Data) return
        val tagIds = interestsSelector.interests.map { it.id }.shuffled().take(4)
        addUserTags(tagIds)
    }

    fun onSelectButtonClicked() {
        addUserTags(selectedIds)
    }

    private fun addUserTags(tags: List<Uuid>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            action.execute {
                profileRepository.addUserTags(tags)
            }.doOnSuccess {
                _command publish Command.CloseScreen
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadInterests() {
        viewModelScope.launch {
            action.execute {
                settingsRepository.update()
            }
        }
    }

    private fun onItemClicked(id: Uuid) {
        val interests = uiState.value.interests
        if (interests !is InterestsSectorTileState.Data) return
        if (!selectedIds.remove(id)) {
            selectedIds.add(id)
        }
        _uiState.update {
            it.copy(
                interests = interests.copy(selectedIds = selectedIds.toList()),
                isSelectButtonEnabled = selectedIds.isNotEmpty(),
            )
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val interests: InterestsSectorTileState = InterestsSectorTileState.Shimmer,
        val isSelectButtonEnabled: Boolean = false,
    )

    sealed class Command {
        object CloseScreen : Command()
    }
}