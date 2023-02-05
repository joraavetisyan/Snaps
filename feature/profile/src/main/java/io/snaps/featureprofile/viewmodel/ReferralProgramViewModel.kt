package io.snaps.featureprofile.viewmodel

import androidx.lifecycle.viewModelScope
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReferralProgramViewModel @Inject constructor(
    mainHeaderHandlerDelegate: MainHeaderHandler,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

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
        _command publish Command.HideBottomDialog
    }

    fun onReferralCodeValueChanged(code: String) {
        _uiState.update {
            it.copy(referralCode = code)
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
        val referralCode: String = "#42GJXE8QM",
        val referralLink: String = "https://djx...edns9m24",
        val bottomDialog: BottomDialog = BottomDialog.ReferralCode,
        val isInviteUserDialogVisibility: Boolean = false,
    ) {
        val isReferralCodeValid get() = referralCode.isNotBlank()
    }

    enum class BottomDialog {
        ReferralCode,
    }

    sealed class Command {
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
    }
}