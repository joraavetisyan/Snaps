package io.snaps.featureprofile.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesources.BottomBarVisibilitySource
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.corecommon.strings.addPrefix
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
    private val bottomBarVisibilitySource: BottomBarVisibilitySource,
    private val notificationsSource: NotificationsSource,
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
                val inviteCode = state.dataOrCache?.ownInviteCode.orEmpty()
                it.copy(
                    referralCode = inviteCode.addPrefix("#"),
                    referralLink = inviteCode.addPrefix("https://snaps.io/"),
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onEnterCodeClicked() = viewModelScope.launch {
        _uiState.update {
            it.copy(bottomDialog = BottomDialog.ReferralCode)
        }
        bottomBarVisibilitySource.updateState(false)
        _command publish Command.ShowBottomDialog
    }

    fun onBottomSheetHidden() {
        bottomBarVisibilitySource.updateState(true)
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

    fun onReferralCodeCopied() {
        viewModelScope.launch {
            notificationsSource.sendMessage(StringKey.ReferralProgramMessageReferralCodeCopied.textValue())
        }
    }

    fun onReferralLinkCopied() {
        viewModelScope.launch {
            notificationsSource.sendMessage(StringKey.ReferralProgramMessageReferralLinkCopied.textValue())
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val referralCode: String = "",
        val referralLink: String = "",
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