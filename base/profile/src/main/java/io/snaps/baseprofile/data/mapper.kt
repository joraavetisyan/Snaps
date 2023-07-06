package io.snaps.baseprofile.data

import io.snaps.baseprofile.data.model.InvitedReferralResponseDto
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.baseprofile.domain.InvitedReferralModel
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.basequests.domain.QuestModel
import io.snaps.corecommon.date.toOffsetLocalDateTime
import io.snaps.corecommon.ext.toCompactDecimalFormat
import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.State
import java.time.ZonedDateTime

fun List<UserInfoResponseDto>.toModelList() = map(UserInfoResponseDto::toModel)

fun UserInfoResponseDto.toModel() = UserInfoModel(
    entityId = entityId,
    createdDate = requireNotNull(ZonedDateTime.parse(createdDate)).toOffsetLocalDateTime(),
    userId = userId,
    email = email,
    wallet = wallet,
    name = name.orEmpty(),
    totalLikes = totalLikes,
    totalSubscribers = totalSubscribers,
    totalSubscriptions = totalSubscriptions,
    avatarUrl = avatarUrl,
    level = level,
    experience = experience,
    inviteCodeRegisteredBy = inviteCodeRegisteredBy,
    ownInviteCode = ownInviteCode,
    totalPublication = null,
    instagramId = instagramId,
    paymentsState = paymentsState,
    firstLevelReferralMultiplier = firstLevelReferralMultiplier ?: 0.03,
    secondLevelReferralMultiplier = secondLevelReferralMultiplier ?: 0.01,
    isUsedTags = isUsedTags ?: true,
    energy = energy ?: 0,
)

fun InvitedReferralResponseDto.toModel() = InvitedReferralModel(
    users = users.toModelList(),
    total = total,
)

fun mainHeaderState(
    profile: State<UserInfoModel>,
    quests: State<QuestModel>,
    isAllGlassesBroken: Boolean,
    snp: CoinValue?,
    bnb: CoinValue?,
    onProfileClicked: () -> Unit,
    onWalletClicked: () -> Unit,
) = when {
    profile is Effect && quests is Effect -> if (profile.isSuccess && quests.isSuccess) {
        MainHeaderState.Data(
            profileImage = profile.requireData.avatar,
            energy = if (!isAllGlassesBroken) {
                quests.requireData.totalEnergyProgress.toString()
            } else "0",
            bnb = bnb?.value?.toCompactDecimalFormat() ?: "-",
            snp = snp?.value?.toCompactDecimalFormat() ?: "-",
            onProfileClicked = onProfileClicked,
            onWalletClicked = onWalletClicked,
        )
    } else MainHeaderState.Error(
        onProfileClicked = onProfileClicked,
        onWalletClicked = onWalletClicked,
    )
    else -> MainHeaderState.Shimmer
}