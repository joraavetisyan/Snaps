package io.snaps.basesession.data

import io.snaps.basenft.data.NftRepository
import io.snaps.corecommon.model.OnboardingType
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.di.Bridged
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface OnboardingHandler {

    val onboardingUiState: StateFlow<UiState>

    val onboardingCommand: Flow<Command>

    fun checkOnboarding(type: OnboardingType)

    fun closeOnboardingDialog()

    data class UiState(
        val onboardingType: OnboardingType? = null,
    )

    sealed interface Command {
        data class OpenDialog(val type: OnboardingType) : Command
        object HideDialog : Command
    }
}

class OnboardingHandlerImplDelegate @Inject constructor(
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val sessionRepository: SessionRepository,
    @Bridged private val nftRepository: NftRepository,
) : OnboardingHandler {

    private val _uiState = MutableStateFlow(OnboardingHandler.UiState())
    override val onboardingUiState = _uiState.asStateFlow()

    private val _command = Channel<OnboardingHandler.Command>()
    override val onboardingCommand = _command.receiveAsFlow()

    override fun checkOnboarding(type: OnboardingType) {
        when {
            type == OnboardingType.Rank -> scope.launch {
                nftRepository.updateNftCollection().doOnSuccess {
                    if (it.isEmpty()) {
                        openDialog(type)
                    }
                }
            }
            !sessionRepository.isOnboardingShown(type) -> scope.launch {
                openDialog(type)
            }
        }
    }

    private suspend fun openDialog(type: OnboardingType) {
        _uiState.update { it.copy(onboardingType = type) }
        _command publish OnboardingHandler.Command.OpenDialog(type)
    }

    override fun closeOnboardingDialog() {
        scope.launch {
            _command publish OnboardingHandler.Command.HideDialog
        }
    }
}