package io.snaps.featuremain.presentation.viewmodel

import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.corecommon.R
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class SocialNetworksViewModel @Inject constructor(
    private val action: Action,
    mainHeaderHandlerDelegate: MainHeaderHandler,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(
        UiState(items = getItems())
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    private fun onDiscordItemClicked() { /*todo*/ }

    private fun onTelegramItemClicked() { /*todo*/ }

    private fun getItems() = listOf(
        CellTileState(
            middlePart = MiddlePart.Data(
                value = StringKey.SocialNetworksTitleDiscord.textValue(),
            ),
            rightPart = RightPart.NavigateNextIcon(),
            leftPart = LeftPart.Logo(
                ImageValue.ResImage(R.drawable.ic_discord)
            ),
            clickListener = { onDiscordItemClicked() },
        ),
        CellTileState(
            middlePart = MiddlePart.Data(
                value = StringKey.SocialNetworksTitleTelegram.textValue(),
            ),
            rightPart = RightPart.NavigateNextIcon(),
            leftPart = LeftPart.Logo(
                ImageValue.ResImage(R.drawable.ic_telegram)
            ),
            clickListener = { onTelegramItemClicked() },
        ),
    )

    data class UiState(
        val items: List<CellTileState>,
    )

    sealed class Command
}