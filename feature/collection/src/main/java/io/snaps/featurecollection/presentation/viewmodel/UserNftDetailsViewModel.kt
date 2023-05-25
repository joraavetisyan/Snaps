package io.snaps.featurecollection.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.basenft.domain.NftModel
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.requireArgs
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UserNftDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @Bridged nftRepository: NftRepository,
) : SimpleViewModel() {

    private val args = savedStateHandle.requireArgs<AppRoute.UserNftDetails.Args>()

    private val _uiState = MutableStateFlow(
        with(requireNotNull(nftRepository.nftCollectionState.value.dataOrCache?.first { it.id == args.nftId })) {
            UiState(title = displayName, nftImage = image, items = getItems(this))
        }
    )
    val uiState = _uiState.asStateFlow()

    private fun getItems(model: NftModel) = listOf(
        CellTileState.Data(
            middlePart = MiddlePart.Data(
                valueBold = model.displayName,
                description = StringKey.NftDetailsMessageDailyReward.textValue(),
            ),
        ),
        CellTileState.Data(
            middlePart = MiddlePart.Data(
                valueBold = StringKey.NftDetailsTitleEarnings.textValue(),
                description = StringKey.NftDetailsDescriptionEarnings.textValue(model.dailyReward.getFormatted()),
            ),
            rightPart = RightPart.ActionIcon(
                source = R.drawable.img_snps.imageValue(),
                tint = Color.Unspecified,
                size = 64.dp,
            )
        ),
        CellTileState.Data(
            middlePart = MiddlePart.Data(
                valueBold = StringKey.NftDetailsTitleCondition.textValue(),
                description = StringKey.NftDetailsMessageCondition.textValue(model.repairCost.getFormatted()),
            ),
            rightPart = RightPart.ActionIcon(
                source = R.drawable.img_repair.imageValue(),
                tint = Color.Unspecified,
                size = 56.dp,
            )
        ),
        CellTileState.Data(
            middlePart = MiddlePart.Data(
                valueBold = StringKey.NftDetailsTitleLevel.textValue(),
                description = StringKey.NftDetailsMessageLevel.textValue(),
            ),
            rightPart = RightPart.ActionIcon(
                source = R.drawable.img_unlock.imageValue(),
                tint = Color.Unspecified,
                size = 56.dp,
            )
        ),
    )

    data class UiState(
        val title: TextValue,
        val nftImage: ImageValue,
        val items: List<CellTileState>,
    )
}