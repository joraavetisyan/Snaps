package io.snaps.featureprofile.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basesettings.data.SettingsRepository
import io.snaps.basesources.remotedata.model.SocialPageType
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.FullUrl
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.coreuitheme.compose.SpecificIcons
import io.snaps.coreuitheme.compose.icons
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
class SocialNetworksViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        settingsRepository.state.onEach {
            if (it is Effect && it.isSuccess) {
                val items = it.requireData.socialPages.map { socialPage ->
                    val icon = icons {
                        when (socialPage.type) {
                            SocialPageType.Discord -> discord
                            SocialPageType.Twitter -> twitter
                            SocialPageType.Telegram -> telegram
                            SocialPageType.Instagram -> instagram
                            else -> infoRounded
                        }
                    }.toImageValue()
                    CellTileState(
                        middlePart = MiddlePart.Data(value = socialPage.type.name.textValue()),
                        rightPart = RightPart.NavigateNextIcon(),
                        leftPart = if (icon.value == SpecificIcons.infoRounded.toImageValue().value) {
                            LeftPart.UiAccentIcon(icon)
                        } else LeftPart.Logo(icon),
                        clickListener = { socialPage.link?.let(::onSocialPageItemClicked) },
                    )
                }
                _uiState.update { it.copy(items = items) }
            }
        }.launchIn(viewModelScope)
    }

    private fun onSocialPageItemClicked(link: FullUrl) {
        viewModelScope.launch { _command publish Command.OpenLink(link) }
    }

    data class UiState(
        val items: List<CellTileState> = emptyList(),
    )

    sealed class Command {
        data class OpenLink(val link: FullUrl) : Command()
    }
}