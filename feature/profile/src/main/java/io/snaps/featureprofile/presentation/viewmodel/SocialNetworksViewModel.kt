package io.snaps.featureprofile.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.strings.capitalizeFirstLetter
import io.snaps.coredata.json.KotlinxSerializationJsonProvider
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.coreuitheme.compose.withIcons
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

@Serializable
internal data class SocialPage(
    @SerialName("type") val type: String,
    @SerialName("link") val link: FullUrl,
)

@HiltViewModel
class SocialNetworksViewModel @Inject constructor() : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        // fetch called in FeatureToggleUpdater, todo to separate source with proper success/failure handle
        FirebaseRemoteConfig.getInstance().getValue("social").run {
            @OptIn(ExperimentalSerializationApi::class)
            KotlinxSerializationJsonProvider().get().decodeFromStream<List<SocialPage>>(asByteArray().inputStream())
        }.map { socialPage ->
            CellTileState(
                middlePart = MiddlePart.Data(value = socialPage.type.capitalizeFirstLetter().textValue()),
                rightPart = RightPart.NavigateNextIcon(),
                leftPart = LeftPart.Logo(
                    withIcons {
                        when (socialPage.type) {
                            "discord" -> discord
                            "twitter" -> twitter
                            "telegram" -> telegram
                            "instagram" -> instagram
                            else -> infoRounded
                        }
                    }.toImageValue()
                ),
                clickListener = { onSocialPageItemClicked(socialPage.link) },
            )
        }.let { items -> _uiState.update { it.copy(items = items) } }
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