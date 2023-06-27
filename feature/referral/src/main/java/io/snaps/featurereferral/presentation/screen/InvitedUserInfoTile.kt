package io.snaps.featurereferral.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.sp
import coil.transform.CircleCropTransformation
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.listtile.CellTileContainer
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.MiddlePartContainer
import io.snaps.coreuicompose.uikit.listtile.MiddlePartTileConfig
import io.snaps.coreuitheme.compose.AppTheme

sealed class InvitedUserInfoTileState : TileState {

    data class Data(
        val id: Uuid,
        val wallet: CryptoAddress,
        val avatar: ImageValue,
        val name: TextValue,
        val energy: Int,
    ) : InvitedUserInfoTileState()

    object Shimmer : InvitedUserInfoTileState()

    data class Error(val onReloadClick: () -> Unit) : InvitedUserInfoTileState()

    @Composable
    override fun Content(modifier: Modifier) {
        InvitedUserInfoTile(modifier, this)
    }
}

@Composable
fun InvitedUserInfoTile(
    modifier: Modifier,
    data: InvitedUserInfoTileState,
) {
    when (data) {
        is InvitedUserInfoTileState.Shimmer -> Shimmer(modifier = modifier, data = data)
        is InvitedUserInfoTileState.Data -> Data(modifier = modifier, data = data)
        is InvitedUserInfoTileState.Error -> Error(modifier = modifier, data = data)
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier,
    data: InvitedUserInfoTileState.Shimmer,
) {
    Content(modifier = modifier) {
        CellTileState(
            leftPart = LeftPart.Shimmer,
            middlePart = MiddlePart.Shimmer(
                needValueLine = true,
                needDescriptionLine = true,
            ),
        ).Content(modifier = Modifier)
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        content()
    }
}

@Composable
private fun Data(
    modifier: Modifier,
    data: InvitedUserInfoTileState.Data,
) {
    val myId = "inlineContent"
    val description = buildAnnotatedString {
        appendInlineContent(myId, "[icon]")
        append(StringKey.ReferralProgramDialogFieldEnergy.textValue(data.energy.toString()).get())
    }
    val inlineContent = mapOf(
        Pair(
            myId,
            InlineTextContent(
                Placeholder(
                    width = MiddlePartTileConfig.descriptionStyle().lineHeight.value.sp,
                    height = MiddlePartTileConfig.descriptionStyle().lineHeight.value.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                Image(
                    painter = R.drawable.img_energy.imageValue().get(),
                    contentDescription = null,
                )
            }
        )
    )
    Content(modifier = modifier) {
        CellTileContainer(
            modifier = modifier,
            hasBadge = false,
            clickListener = null,
        ) {
            data.avatar.let {
                LeftPart.Logo(it) {
                    transformations(CircleCropTransformation())
                }.Content(modifier = Modifier)
            }
            MiddlePartContainer(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.name.get(),
                    color = AppTheme.specificColorScheme.textPrimary,
                    style = MiddlePartTileConfig.valueStyle(),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                )
                Text(
                    text = description,
                    inlineContent = inlineContent,
                    color = AppTheme.specificColorScheme.textSecondary,
                    style = MiddlePartTileConfig.descriptionStyle(),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun Error(
    modifier: Modifier,
    data: InvitedUserInfoTileState.Error,
) {
    Content(modifier = modifier) {
        MessageBannerState
            .defaultState(onClick = data.onReloadClick)
            .Content(modifier = modifier)
    }
}