package io.snaps.featurereferral.presentation

import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featurereferral.presentation.screen.ReferralsTileState

fun State<List<UserInfoModel>>.toReferralsUiState(
    onReferralClick: (UserInfoModel) -> Unit,
    onShowQrClick: () -> Unit,
    onReloadClick: () -> Unit,
): ReferralsTileState {
    return when (this) {
        is Loading -> ReferralsTileState.Shimmer(
            onShowQrClick = onShowQrClick,
        )
        is Effect -> if (isSuccess) {
            if (requireData.isEmpty()) ReferralsTileState.Empty(
                onShowQrClick = onShowQrClick,
            )
            else ReferralsTileState.Data(
                values = requireData,
                onReferralClick = onReferralClick,
                onShowQrClick = onShowQrClick,
            )
        } else ReferralsTileState.Error(
            onReloadClick = onReloadClick,
            onShowQrClick = onShowQrClick,
        )
    }
}