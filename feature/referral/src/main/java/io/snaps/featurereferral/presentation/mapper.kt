package io.snaps.featurereferral.presentation

import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.featurereferral.presentation.viewmodel.ReferralsUiState

fun State<List<UserInfoModel>>.toReferralsUiState(): ReferralsUiState {
    return when (this) {
        is Loading -> ReferralsUiState.Shimmer
        is Effect -> if (isSuccess) {
            if (requireData.isEmpty()) ReferralsUiState.Empty
            else ReferralsUiState.Data(requireData)
        } else ReferralsUiState.Error
    }
}