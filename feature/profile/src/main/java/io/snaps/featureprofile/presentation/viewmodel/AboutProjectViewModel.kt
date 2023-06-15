package io.snaps.featureprofile.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.FullUrl
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutProjectViewModel @Inject constructor() : SimpleViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(items = getItems())
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private fun getItems() = listOf(
        CellTileState(
            middlePart = MiddlePart.Data(
                value = StringKey.AboutProjectTitlePrivacyPolicy.textValue(),
            ),
            rightPart = RightPart.NavigateNextIcon(),
            clickListener = ::onPrivacyPolicyClicked,
        ),
        CellTileState(
            middlePart = MiddlePart.Data(
                value = StringKey.AboutProjectTitleTermsOfUse.textValue(),
            ),
            rightPart = RightPart.NavigateNextIcon(),
            clickListener = ::onTermsOfUserClicked,
        ),
        CellTileState(
            middlePart = MiddlePart.Data(
                value = StringKey.AboutProjectTitleWhitepaper.textValue(),
            ),
            rightPart = RightPart.NavigateNextIcon(),
            clickListener = ::onWhitepaperClicked,
        ),
    )

    private fun onPrivacyPolicyClicked() {
        viewModelScope.launch {
            _command publish Command.OpenLink("https://snaps-docs.gitbook.io/privacy-policy-snaps/")
        }
    }

    private fun onTermsOfUserClicked() {
        viewModelScope.launch {
            _command publish Command.OpenLink("https://snaps-docs.gitbook.io/terms-of-service/")
        }
    }

    private fun onWhitepaperClicked() {
        viewModelScope.launch {
            _command publish Command.OpenLink("https://snaps-docs.gitbook.io/snaps-whitepaper-ru/general/chto-takoe-snaps/")
        }
    }

    data class UiState(
        val items: List<CellTileState>,
    )

    sealed class Command {
        data class OpenLink(val link: FullUrl) : Command()
    }
}