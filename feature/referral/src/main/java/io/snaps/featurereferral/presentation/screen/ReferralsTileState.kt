package io.snaps.featurereferral.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
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

    data class Data(
        val values: List<UserInfoModel>,
        val onReferralClick: (UserInfoModel) -> Unit,
        val onShowQrClick: () -> Unit,
    ) : ReferralsTileState()

    object Empty : ReferralsTileState()

    object Shimmer : ReferralsTileState()

    data class Error(
        val onReloadClick: () -> Unit,
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
        ReferralsTileState.Shimmer -> Shimmer(modifier = modifier)
        is ReferralsTileState.Data -> Data(modifier = modifier, data = data)
        ReferralsTileState.Empty -> Empty(modifier = modifier)
        is ReferralsTileState.Error -> Error(modifier = modifier, data = data)
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier,
) {
    Column(modifier = modifier) {
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
private fun Data(
    modifier: Modifier,
    data: ReferralsTileState.Data,
) {
    Column(modifier = modifier) {
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
        SimpleButtonActionL(
            onClick = data.onShowQrClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = LocalBottomNavigationHeight.current),
        ) {
            SimpleButtonContent(text = "Referral program".textValue())
        }
    }
}

@Composable
private fun Empty(
    modifier: Modifier,
) {
    EmptyListTileState(
        image = ImageValue.ResImage(R.drawable.img_guy_confused),
        title = StringKey.ReferralProgramTitleNoReferrals.textValue(),
        message = StringKey.ReferralProgramMessageNoReferrals.textValue(),
    ).Content(modifier = modifier)
}

@Composable
private fun Error(
    modifier: Modifier,
    data: ReferralsTileState.Error,
) {
    MessageBannerState
        .defaultState(onClick = data.onReloadClick)
        .Content(modifier = modifier)
}