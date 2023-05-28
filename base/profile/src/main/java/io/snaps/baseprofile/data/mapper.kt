package io.snaps.baseprofile.data

import io.snaps.baseprofile.data.model.QuestInfoResponseDto
import io.snaps.baseprofile.data.model.QuestItemDto
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.baseprofile.domain.QuestInfoModel
import io.snaps.baseprofile.domain.QuestModel
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.R
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.date.toOffsetLocalDateTime
import io.snaps.corecommon.ext.toCompactDecimalFormat
import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.State
import java.lang.Integer.min
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
    avatar = avatarUrl?.imageValue() ?: R.drawable.img_avatar.imageValue(),
    level = level,
    experience = experience,
    questInfo = questInfo?.toQuestInfoModel(),
    inviteCodeRegisteredBy = inviteCodeRegisteredBy,
    ownInviteCode = ownInviteCode,
    totalPublication = null,
    instagramId = instagramId,
    paymentsState = paymentsState,
    firstLevelReferralMultiplier = firstLevelReferralMultiplier ?: 0.03,
    secondLevelReferralMultiplier = secondLevelReferralMultiplier ?: 0.01,
)

fun QuestInfoResponseDto.toQuestInfoModel() = QuestInfoModel(
    quests = quests.map(QuestItemDto::toQuestModel),
    questDate = requireNotNull(ZonedDateTime.parse(questDate)).toOffsetLocalDateTime(),
    totalEnergy = quests.sumOf { it.quest.energy },
    totalEnergyProgress = quests
        .filter { it.energyProgress() == it.quest.energy }
        .sumOf { it.quest.energy },
)

fun QuestItemDto.toQuestModel() = QuestModel(
    energy = quest.energy,
    type = quest.type,
    completed = completed,
    status = status,
    count = quest.count,
    madeCount = madeCount,
)

// todo must be on back
private fun QuestItemDto.energyProgress(): Int {
    return if (madeCount != null && quest.count != null) {
        val madeByOne = madeCount.toDouble() / quest.count.toDouble() * quest.energy
        madeByOne.toInt()
    } else {
        if (completed) {
            quest.energy
        } else 0
    }
}

fun mainHeaderState(
    profile: State<UserInfoModel>,
    isAllGlassesBroken: Boolean,
    snp: CoinValue?,
    bnb: CoinValue?,
    onProfileClicked: () -> Unit,
    onWalletClicked: () -> Unit,
) = when (profile) {
    is Effect -> if (profile.isSuccess) {
        MainHeaderState.Data(
            profileImage = profile.requireData.avatar,
            energy = if (!isAllGlassesBroken) {
                profile.requireData.questInfo?.totalEnergyProgress.toString()
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