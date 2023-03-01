package io.snaps.featureprofile.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.publish
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
class ReferralProgramViewModel @Inject constructor(
    mainHeaderHandlerDelegate: MainHeaderHandler,
    private val profileRepository: ProfileRepository,
    private val action: Action,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        subscribeOnCurrentUser()
    }

    private fun subscribeOnCurrentUser() {
        profileRepository.state.onEach { state ->
            _uiState.update {
                it.copy(
                    referralCode = state.dataOrCache?.ownInviteCode.orEmpty(),
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onEnterCodeClicked() = viewModelScope.launch {
        _uiState.update {
            it.copy(bottomDialog = BottomDialog.ReferralCode)
        }
        _command publish Command.ShowBottomDialog
    }

    fun onInviteUserButtonClicked() {
        _uiState.update {
            it.copy(isInviteUserDialogVisibility = true)
        }
    }

    fun onReferralCodeDialogButtonClicked() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        action.execute {
            profileRepository.setInviteCode(uiState.value.inviteCodeValue)
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }.doOnSuccess {
            _command publish Command.HideBottomDialog
        }
    }

    fun onInviteCodeValueChanged(code: String) {
        _uiState.update {
            it.copy(inviteCodeValue = code)
        }
    }

    fun onDismissRequest() {
        _uiState.update {
            it.copy(isInviteUserDialogVisibility = false)
        }
    }

    fun onCloseDialogClicked() {
        _uiState.update {
            it.copy(isInviteUserDialogVisibility = false)
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val referralCode: String = "",
        val referralLink: String = "https://djx...edns9m24",
        val inviteCodeValue: String = "",
        val bottomDialog: BottomDialog = BottomDialog.ReferralCode,
        val isInviteUserDialogVisibility: Boolean = false,
    ) {
        val isReferralCodeValid get() = inviteCodeValue.isNotBlank()
    }

    enum class BottomDialog {
        ReferralCode,
    }

    sealed class Command {
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
    }
}