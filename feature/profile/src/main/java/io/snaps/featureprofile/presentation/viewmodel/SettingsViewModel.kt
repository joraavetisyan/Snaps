package io.snaps.featureprofile.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basesession.data.SessionRepository
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(items = getItems())
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onDeleteAccountClicked() {
        _uiState.update { it.copy(dialog = Dialog.ConfirmDeleteAccount) }
    }

    // todo delete account api request
    fun onDeleteAccountConfirmed() {
        _uiState.update { it.copy(dialog = null) }
    }

    fun onLogoutClicked() {
        _uiState.update { it.copy(dialog = Dialog.ConfirmLogout) }
    }

    fun onLogoutConfirmed() {
        _uiState.update { it.copy(isLoading = true, dialog = null) }
        sessionRepository.logout()
    }

    fun onDialogDismissRequest() = viewModelScope.launch {
        _uiState.update { it.copy(dialog = null) }
    }

    private fun onWalletItemClicked() = viewModelScope.launch {
        _command publish Command.OpenWalletSettingsScreen
    }

    private fun onEditProfileClicked() = viewModelScope.launch {
        _command publish Command.OpenEditProfileScreen
    }

    private fun onReferralProgramItemClicked() = viewModelScope.launch {
        _command publish Command.OpenReferralProgramScreen
    }

    private fun onSocialNetworksItemClicked() = viewModelScope.launch {
        _command publish Command.OpenSocialNetworksScreen
    }

    private fun onAboutProjectItemClicked() = viewModelScope.launch {
        _command publish Command.OpenAboutProjectScreen
    }

    private fun getItems() = listOf(
        CellTileState(
            middlePart = MiddlePart.Data(
                value = StringKey.EditProfileTitle.textValue(),
            ),
            rightPart = RightPart.NavigateNextIcon(),
            clickListener = { onEditProfileClicked() },
        ),
        CellTileState(
            middlePart = MiddlePart.Data(
                value = StringKey.SettingsTitleWallet.textValue(),
            ),
            rightPart = RightPart.NavigateNextIcon(),
            clickListener = { onWalletItemClicked() },
        ),
        /*CellTileState(
            middlePart = MiddlePart.Data(
                value = StringKey.SettingsTitleReferralProgram.textValue(),
            ),
            rightPart = RightPart.NavigateNextIcon(),
            clickListener = { onReferralProgramItemClicked() },
        ),*/
        CellTileState(
            middlePart = MiddlePart.Data(
                value = StringKey.SettingsTitleSocialNetworks.textValue(),
            ),
            rightPart = RightPart.NavigateNextIcon(),
            clickListener = { onSocialNetworksItemClicked() },
        ),
        CellTileState(
            middlePart = MiddlePart.Data(
                value = StringKey.SettingsTitleAboutProject.textValue(),
            ),
            rightPart = RightPart.NavigateNextIcon(),
            clickListener = { onAboutProjectItemClicked() },
        ),
    )

    data class UiState(
        val isLoading: Boolean = false,
        val items: List<CellTileState>,
        val dialog: Dialog? = null,
    )

    sealed class Dialog {
        object ConfirmLogout : Dialog()
        object ConfirmDeleteAccount : Dialog()
    }

    sealed class Command {
        object OpenWalletSettingsScreen : Command()
        object OpenReferralProgramScreen : Command()
        object OpenSocialNetworksScreen : Command()
        object OpenAboutProjectScreen : Command()
        object OpenEditProfileScreen : Command()
    }
}