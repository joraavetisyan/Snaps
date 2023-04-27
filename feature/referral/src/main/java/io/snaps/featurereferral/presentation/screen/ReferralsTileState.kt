package io.snaps.featurereferral.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.TileState
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
        ReferralsTileState.Shimmer -> Shimmer()
        is ReferralsTileState.Data -> Data(data)
        ReferralsTileState.Empty -> Empty()
        is ReferralsTileState.Error -> Error(data)
    }
}

@Composable
private fun Shimmer() {
    Column {
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
    data: ReferralsTileState.Data,
) {
    LazyColumn {
        items(data.values, key = { it.entityId }) { model ->
            CellTileState(
                leftPart = model.avatar?.let {
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

@Composable
private fun Empty() {
    EmptyListTileState(
        image = ImageValue.ResImage(R.drawable.img_guy_confused),
        title = StringKey.ReferralProgramTitleNoReferrals.textValue(),
        message = StringKey.ReferralProgramMessageNoReferrals.textValue(),
    ).Content(modifier = Modifier)
}

@Composable
private fun Error(data: ReferralsTileState.Error) {
    MessageBannerState
        .defaultState(onClick = data.onReloadClick)
        .Content(modifier = Modifier)
}