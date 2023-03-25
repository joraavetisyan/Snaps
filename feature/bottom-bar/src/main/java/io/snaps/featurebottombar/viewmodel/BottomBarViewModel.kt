package io.snaps.featurebottombar.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.basesession.AppRouteProvider
import io.snaps.basesources.BottomBarVisibilitySource
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.base.ROUTE_ARGS_SEPARATOR
import io.snaps.coreui.viewmodel.SimpleViewModel
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
class BottomBarViewModel @Inject constructor(
    private val appRouteProvider: AppRouteProvider,
    private val nftRepository: NftRepository,
    private val action: Action,
    bottomBarVisibilitySource: BottomBarVisibilitySource,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        subscribeOnCountBrokenGlasses()

        bottomBarVisibilitySource.state.onEach { isBottomBarVisible ->
            _uiState.update { it.copy(isBottomBarVisible = isBottomBarVisible) }
        }.launchIn(viewModelScope)

        loadUserNft()
    }

    private fun subscribeOnCountBrokenGlasses() = viewModelScope.launch {
        nftRepository.countBrokenGlassesState.onEach { state ->
            _uiState.update {
                it.copy(
                    badgeText = state.dataOrCache?.takeIf { it > 0 }?.toString().orEmpty(),
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun loadUserNft() = viewModelScope.launch {
        action.execute {
            nftRepository.updateNftCollection()
        }
    }

    fun updateMenuRoute(path: String?) {
        val route = path?.takeWhile { it != ROUTE_ARGS_SEPARATOR } ?: return
        appRouteProvider.updateMenuRouteState(route)
    }

    data class UiState(
        val isBottomBarVisible: Boolean = true,
        val badgeText: String = "",
    )

    sealed interface Command
}