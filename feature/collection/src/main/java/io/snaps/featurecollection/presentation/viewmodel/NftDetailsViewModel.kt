package io.snaps.featurecollection.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.corecommon.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NftDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : SimpleViewModel() {

    private val args = savedStateHandle.requireArgs<AppRoute.NftDetails.Args>()

    private val _uiState = MutableStateFlow(
        UiState(
            nftImage = ImageValue.Url(args.image),
            items = getItems(),
        )
    )
    val uiState = _uiState.asStateFlow()

    private fun getItems() = listOf(
        CellTileState.Data(
            middlePart = MiddlePart.Data(
                valueBold = args.type.name.textValue(),
                description = StringKey.NftDetailsDescriptionDailyReward.textValue(),
            ),
        ),
        CellTileState.Data(
            middlePart = MiddlePart.Data(
                valueBold = StringKey.NftDetailsTitleEarnings.textValue(),
                description = StringKey.NftDetailsDescriptionEarnings.textValue(
                    args.dailyReward.toString()
                ),
            ),
            rightPart = RightPart.ActionIcon(
                source = ImageValue.ResImage(R.drawable.img_snps),
                tint = Color.Unspecified,
                size = 64.dp,
            )
        ),
        CellTileState.Data(
            middlePart = MiddlePart.Data(
                valueBold = StringKey.NftDetailsTitleCondition.textValue(),
                description = StringKey.NftDetailsDescriptionCondition.textValue(),
            ),
            rightPart = RightPart.ActionIcon(
                source = ImageValue.ResImage(R.drawable.img_repair),
                tint = Color.Unspecified,
                size = 56.dp,
            )
        ),
        CellTileState.Data(
            middlePart = MiddlePart.Data(
                valueBold = StringKey.NftDetailsTitleLevel.textValue(),
                description = StringKey.NftDetailsDescriptionLevel.textValue(),
            ),
            rightPart = RightPart.ActionIcon(
                source = ImageValue.ResImage(R.drawable.img_unlock),
                tint = Color.Unspecified,
                size = 56.dp,
            )
        ),
    )

    data class UiState(
        val nftImage: ImageValue,
        val items: List<CellTileState>,
    )
}