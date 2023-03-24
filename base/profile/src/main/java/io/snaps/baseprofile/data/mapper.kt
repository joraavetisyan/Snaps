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
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.State
import java.time.ZonedDateTime

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
    questInfo = questInfo.toQuestInfoModel(),
    inviteCodeRegisteredBy = inviteCodeRegisteredBy,
    ownInviteCode = ownInviteCode,
    totalPublication = null,
)

fun BalanceResponseDto.toModel() = BalanceModel(
    unlocked = unlockedTokensBalance,
    locked = lockedTokensBalance,
    exchangeRate = exchangeRate,
)

fun QuestInfoResponseDto.toQuestInfoModel() = QuestInfoModel(
    quests = quests.map(QuestItemDto::toQuestModel),
    questDate = requireNotNull(ZonedDateTime.parse(questDate)).toOffsetLocalDateTime(),
    totalEnergy = energy,
    totalEnergyProgress = quests.sumOf { it.energyProgress }, // todo,
)

fun QuestItemDto.toQuestModel() = QuestModel(
    energyProgress = energyProgress,
    energy = quest.energy,
    type = quest.type,
    completed = completed,
    network = network,
    count = quest.count,
    madeCount = madeCount,
)

fun mainHeaderState(
    profile: State<UserInfoModel>,
    coins: State<BalanceModel>,
    onProfileClicked: () -> Unit,
    onWalletClicked: () -> Unit,
) = if (profile is Effect && coins is Effect) {
    if (profile.isSuccess && coins.isSuccess) {
        MainHeaderState.Data(
            profileImage = profile.requireData.avatar,
            energy = profile.requireData.questInfo.totalEnergy.toString(),
            unlocked = coins.requireData.unlocked.toString(),
            locked = coins.requireData.locked.toString(),
            onProfileClicked = onProfileClicked,
            onWalletClicked = onWalletClicked,
        )
    } else MainHeaderState.Error
} else {
    MainHeaderState.Shimmer
}