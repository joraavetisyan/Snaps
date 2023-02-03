package io.snaps.featuremain.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.network.Action
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
class WalletSettingsViewModel @Inject constructor(
    private val action: Action,
    mainHeaderHandlerDelegate: MainHeaderHandler,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(
        UiState(items = getItems())
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onLookButtonClicked() = viewModelScope.launch {
        _uiState.update {
            it.copy(isDialogVisibility = false)
        }
        _command publish Command.OpenBackupWalletKeyScreen
    }

    fun onCloseDialogButtonClicked() {
        _uiState.update {
            it.copy(isDialogVisibility = false)
        }
    }

    fun onDismissRequest() {
        _uiState.update {
            it.copy(isDialogVisibility = false)
        }
    }

    private fun onBackupItemClicked() {
        _uiState.update {
            it.copy(isDialogVisibility = true,)
        }
    }

    private fun getItems() = listOf(
        CellTileState(
            middlePart = MiddlePart.Data(
                value = StringKey.WalletSettingsTitleBackup.textValue(),
                description = StringKey.WalletSettingsDescriptionBackup.textValue(),
            ),
            rightPart = RightPart.NavigateNextIcon,
            clickListener = { onBackupItemClicked() },
        ),
    )

    data class UiState(
        val items: List<CellTileState>,
        val isDialogVisibility: Boolean = false,
    )

    sealed class Command {
        object OpenBackupWalletKeyScreen : Command()
    }
}