package io.snaps.featurereferral.presentation

import io.snaps.basenft.domain.NftModel
import io.snaps.basenft.ui.CollectionItemState
import io.snaps.baseprofile.domain.InvitedReferralModel
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.ext.toPercentageFormat
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featurereferral.presentation.screen.ReferralsTileState

fun State<InvitedReferralModel>.toReferralsUiState(
    onReferralClick: (UserInfoModel) -> Unit,
    onShowQrClick: () -> Unit,
    onReloadClick: () -> Unit,
): ReferralsTileState {
    return when (this) {
        is Loading -> ReferralsTileState.Shimmer(
            onShowQrClick = onShowQrClick,
        )
        is Effect -> if (isSuccess) {
            if (requireData.users.isEmpty()) ReferralsTileState.Empty(
                onShowQrClick = onShowQrClick,
            )
            else ReferralsTileState.Data(
                values = requireData.users,
                onReferralClick = onReferralClick,
                onShowQrClick = onShowQrClick,
            )
        } else ReferralsTileState.Error(
            onReloadClick = onReloadClick,
            onShowQrClick = onShowQrClick,
        )
    }
}

fun NftModel.toNftCollectionItemState(
    snpsUsdExchangeRate: Double,
) = CollectionItemState.Nft(
    type = type,
    image = image,
    dailyReward = dailyReward.toFiat(rate = snpsUsdExchangeRate),
    dailyUnlock = dailyUnlock.toPercentageFormat(),
    dailyConsumption = dailyConsumption.toPercentageFormat(),
    isHealthBadgeVisible = !isHealthy,
    isRepairable = false, // repair is not displayed on the tasks screen
    level = level,
    experience = experience,
    bonus = bonus,
    upperThreshold = upperThreshold,
    lowerThreshold = lowerThreshold,
    isLevelVisible = false, // level is not displayed on the tasks screen
    onRepairClicked = {},
    onItemClicked = {},
    onHelpIconClicked = {},
)