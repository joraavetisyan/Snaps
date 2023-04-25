package io.snaps.baseprofile.data

import io.snaps.baseprofile.data.model.BalanceResponseDto
import io.snaps.baseprofile.data.model.QuestInfoResponseDto
import io.snaps.baseprofile.data.model.QuestItemDto
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.baseprofile.domain.BalanceModel
import io.snaps.baseprofile.domain.QuestInfoModel
import io.snaps.baseprofile.domain.QuestModel
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.date.toOffsetLocalDateTime
import io.snaps.corecommon.ext.round
import io.snaps.corecommon.ext.toStringValue
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
    avatar = avatarUrl?.let(ImageValue::Url),
    level = level,
    experience = experience,
    questInfo = questInfo?.toQuestInfoModel(),
    inviteCodeRegisteredBy = inviteCodeRegisteredBy,
    ownInviteCode = ownInviteCode,
    totalPublication = null,
    instagramId = instagramId,
)

fun BalanceResponseDto.toModel() = BalanceModel(
    unlocked = unlockedTokensBalance,
    locked = lockedTokensBalance,
    exchangeRate = exchangeRate,
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

private fun QuestItemDto.energyProgress(): Int {
    return if (madeCount != null && quest.count != null) {
        val madeByOne = madeCount.toDouble() / quest.count.toDouble() * 20
        min(madeByOne.toInt(), quest.energy)
    } else {
        if (completed) {
            quest.energy
        } else 0
    }
}

fun mainHeaderState(
    profile: State<UserInfoModel>,
    coins: State<BalanceModel>,
    onProfileClicked: () -> Unit,
    onWalletClicked: () -> Unit,
) = if (profile is Effect && coins is Effect) {
    if (profile.isSuccess && coins.isSuccess) {
        MainHeaderState.Data(
            profileImage = profile.requireData.avatar,
            energy = profile.requireData.questInfo?.totalEnergyProgress.toString(),
            unlocked = coins.requireData.unlocked.round().toStringValue(),
            locked = coins.requireData.locked.round().toStringValue(),
            onProfileClicked = onProfileClicked,
            onWalletClicked = onWalletClicked,
        )
    } else MainHeaderState.Error(
        onProfileClicked = onProfileClicked,
        onWalletClicked = onWalletClicked,
    )
} else {
    MainHeaderState.Shimmer
}