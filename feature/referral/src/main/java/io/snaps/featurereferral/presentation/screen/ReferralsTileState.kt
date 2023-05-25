package io.snaps.featurereferral.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.LocalBottomNavigationHeight
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionL
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.EmptyListTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.coreuitheme.compose.AppTheme

sealed class ReferralsTileState : TileState {

    abstract val onShowQrClick: () -> Unit

    data class Data(
        val values: List<UserInfoModel>,
        val onReferralClick: (UserInfoModel) -> Unit,
        override val onShowQrClick: () -> Unit,
    ) : ReferralsTileState()

    data class Empty(
        override val onShowQrClick: () -> Unit,
    ) : ReferralsTileState()

    data class Shimmer(
        override val onShowQrClick: () -> Unit,
    ) : ReferralsTileState()

    data class Error(
        val onReloadClick: () -> Unit,
        override val onShowQrClick: () -> Unit,
    ) : ReferralsTileState()

    @Composable
    override fun Content(modifier: Modifier) {
        ReferralsTile(modifier, this)
    }
}

@Composable
fun ReferralsTile(
    modifier: Modifier,
    data: ReferralsTileState,
) {
    when (data) {
        is ReferralsTileState.Shimmer -> Shimmer(modifier = modifier, data = data)
        is ReferralsTileState.Data -> Data(modifier = modifier, data = data)
        is ReferralsTileState.Empty -> Empty(modifier = modifier, data = data)
        is ReferralsTileState.Error -> Error(modifier = modifier, data = data)
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier,
    data: ReferralsTileState.Shimmer,
) {
    Content(modifier = modifier, data = data) {
        repeat(3) {
            CellTileState(
                leftPart = LeftPart.Shimmer,
                middlePart = MiddlePart.Shimmer(
                    needValueLine = true,
                ),
                rightPart = RightPart.Shimmer(needLine = true),
            ).Content(modifier = Modifier)
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    data: ReferralsTileState,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        content()
        SimpleButtonActionL(
            onClick = data.onShowQrClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = LocalBottomNavigationHeight.current),
        ) {
            SimpleButtonContent(text = StringKey.ReferralProgramActionReferralProgram.textValue())
        }
    }
}

@Composable
private fun Data(
    modifier: Modifier,
    data: ReferralsTileState.Data,
) {
    Content(modifier = modifier, data = data) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            data.values.forEach { model ->
                CellTileState(
                    leftPart = model.avatar.let {
                        LeftPart.Logo(it) {
                            transformations(CircleCropTransformation())
                        }
                    },
                    middlePart = MiddlePart.Data(
                        value = model.name.textValue(),
                    ),
                    rightPart = RightPart.ActionIcon(
                        source = AppTheme.specificIcons.forward.toImageValue(),
                    ),
                    clickListener = { data.onReferralClick(model) },
                ).Content(modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
private fun Empty(
    modifier: Modifier,
    data: ReferralsTileState.Empty,
) {
    Content(modifier = modifier, data = data) {
        EmptyListTileState.defaultState(
            title = StringKey.ReferralProgramTitleNoReferrals.textValue(),
            message = StringKey.ReferralProgramMessageNoReferrals.textValue(),
        ).Content(modifier = modifier)
    }
}

@Composable
private fun Error(
    modifier: Modifier,
    data: ReferralsTileState.Error,
) {
    Content(modifier = modifier, data = data) {
        MessageBannerState
            .defaultState(onClick = data.onReloadClick)
            .Content(modifier = modifier)
    }
}