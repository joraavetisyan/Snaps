package io.snaps.featurereferral.presentation

import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featurereferral.presentation.screen.ReferralsTileState

fun State<List<UserInfoModel>>.toReferralsUiState(
    onReferralClick: (UserInfoModel) -> Unit,
    onReloadClick: () -> Unit,
): ReferralsTileState {
    return when (this) {
        is Loading -> ReferralsTileState.Shimmer
        is Effect -> if (isSuccess) {
            if (requireData.isEmpty()) ReferralsTileState.Empty
            else ReferralsTileState.Data(requireData, onReferralClick)
        } else ReferralsTileState.Error(onReloadClick)
    }
}