package io.snaps.basequests.data

import io.snaps.basequests.data.model.QuestItemDto
import io.snaps.basequests.data.model.QuestItemResponseDto
import io.snaps.basequests.domain.QuestInfoModel
import io.snaps.basequests.domain.QuestModel
import io.snaps.corecommon.date.toOffsetLocalDateTime
import java.time.ZonedDateTime

fun List<QuestItemResponseDto>.toModelList() = map(QuestItemResponseDto::toQuestModel)

fun QuestItemResponseDto.toQuestModel() = QuestModel(
    id = id,
    userId = userId,
    roundId = roundId,
    totalEnergy = quests.sumOf { it.quest.energy },
    totalEnergyProgress = quests
        .filter { it.energyProgress() == it.quest.energy }
        .sumOf { it.quest.energy },
    questDate = requireNotNull(ZonedDateTime.parse(questDate)).toOffsetLocalDateTime(),
    experience = experience,
    quests = quests.map(QuestItemDto::toQuestInfoModel),
)

private fun QuestItemDto.toQuestInfoModel() = QuestInfoModel(
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