package io.snaps.featureinitialization.presentation

import io.snaps.basesettings.data.model.InterestDto
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Uuid
import io.snaps.featureinitialization.presentation.screen.InterestsSectorTileState

fun State<List<InterestDto>>.toInterestsSectorTileState(
    selectedIds: List<Uuid>,
    onReloadClick: () -> Unit,
    onItemClick: (Uuid) -> Unit,
): InterestsSectorTileState {
    return when (this) {
        is Loading -> InterestsSectorTileState.Shimmer
        is Effect -> if (isSuccess) {
            InterestsSectorTileState.Data(
                interests = requireData,
                selectedIds = selectedIds,
                onItemClick = onItemClick,
            )
        } else InterestsSectorTileState.Error(onReloadClick = onReloadClick)
    }
}