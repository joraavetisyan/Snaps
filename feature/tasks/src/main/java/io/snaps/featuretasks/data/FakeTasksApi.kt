package io.snaps.featuretasks.data

import io.snaps.baseprofile.data.model.QuestDto
import io.snaps.baseprofile.data.model.QuestItemDto
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rInt
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.DateTime
import io.snaps.corecommon.model.QuestType
import io.snaps.coredata.network.BaseResponse
import io.snaps.featuretasks.data.model.HistoryTaskItemResponseDto
import kotlinx.coroutines.delay
import retrofit2.http.Query

class FakeTasksApi : TasksApi {

    override suspend fun historyTasks(
        @Query(value = "from") from: DateTime?,
        @Query(value = "count") count: Int
    ): BaseResponse<List<HistoryTaskItemResponseDto>> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = getTasks()
        ).also {
            delay(mockDelay)
        }
    }

    override suspend fun instagramPost(): BaseResponse<Completable> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = Completable
        ).also {
            delay(mockDelay)
        }
    }

    private fun getTasks() = listOf(
        HistoryTaskItemResponseDto(
            id = "1",
            userId = "1",
            energy = 20,
            experience = 0,
            date = "2023-03-01T00:00:00+00:00",
            quests = listOf(
                QuestItemDto(
                    energyProgress = 2,
                    completed = true,
                    done = null,
                    madeCount = rInt,
                    status = null,
                    quest = QuestDto(
                        count = 20,
                        type = QuestType.Like,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 2,
                    completed = false,
                    madeCount = rInt,
                    done = null,
                    status = null,
                    quest = QuestDto(
                        count = rInt,
                        type = QuestType.PublishVideo,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 0,
                    completed = false,
                    done = null,
                    madeCount = 0,
                    status = null,
                    quest = QuestDto(
                        count = 20,
                        type = QuestType.Watch,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 1,
                    completed = false,
                    madeCount = 0,
                    done = null,
                    status = null,
                    quest = QuestDto(
                        count = 5,
                        type = QuestType.Subscribe,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 1,
                    completed = false,
                    madeCount = rInt,
                    status = null,
                    done = null,
                    quest = QuestDto(
                        count = rInt,
                        type = QuestType.SocialPost,
                        energy = 20,
                    )
                ),
            )
        ),
        HistoryTaskItemResponseDto(
            id = "2",
            userId = "2",
            energy = 20,
            experience = 0,
            date = "2023-03-01T00:00:00+00:00",
            quests = listOf(
                QuestItemDto(
                    energyProgress = 2,
                    completed = true,
                    madeCount = rInt,
                    done = null,
                    status = null,
                    quest = QuestDto(
                        count = 20,
                        type = QuestType.Like,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 2,
                    completed = false,
                    madeCount = rInt,
                    status = null,
                    done = null,
                    quest = QuestDto(
                        count = rInt,
                        type = QuestType.PublishVideo,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 0,
                    completed = false,
                    done = null,
                    madeCount = 0,
                    status = null,
                    quest = QuestDto(
                        count = 20,
                        type = QuestType.Watch,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 1,
                    completed = false,
                    madeCount = 0,
                    status = null,
                    done = null,
                    quest = QuestDto(
                        count = 5,
                        type = QuestType.Subscribe,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 1,
                    completed = false,
                    done = null,
                    madeCount = rInt,
                    status = null,
                    quest = QuestDto(
                        count = rInt,
                        type = QuestType.SocialPost,
                        energy = 20,
                    )
                ),
            )
        ),
        HistoryTaskItemResponseDto(
            id = "3",
            userId = "3",
            energy = 20,
            experience = 0,
            date = "2023-03-01T00:00:00+00:00",
            quests = listOf(
                QuestItemDto(
                    energyProgress = 2,
                    completed = true,
                    done = null,
                    madeCount = rInt,
                    status = null,
                    quest = QuestDto(
                        count = 20,
                        type = QuestType.Like,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 2,
                    completed = false,
                    done = null,
                    madeCount = rInt,
                    status = null,
                    quest = QuestDto(
                        count = rInt,
                        type = QuestType.PublishVideo,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 0,
                    completed = false,
                    done = null,
                    madeCount = 0,
                    status = null,
                    quest = QuestDto(
                        count = 20,
                        type = QuestType.Watch,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 1,
                    completed = false,
                    done = null,
                    madeCount = 0,
                    status = null,
                    quest = QuestDto(
                        count = 5,
                        type = QuestType.Subscribe,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 1,
                    completed = false,
                    done = null,
                    madeCount = rInt,
                    status = null,
                    quest = QuestDto(
                        count = rInt,
                        type = QuestType.SocialPost,
                        energy = 20,
                    )
                ),
            )
        ),
        HistoryTaskItemResponseDto(
            id = "4",
            userId = "4",
            energy = 20,
            experience = 0,
            date = "2023-03-01T00:00:00+00:00",
            quests = listOf(
                QuestItemDto(
                    energyProgress = 2,
                    completed = true,
                    done = null,
                    madeCount = rInt,
                    status = null,
                    quest = QuestDto(
                        count = 20,
                        type = QuestType.Like,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 2,
                    completed = false,
                    done = null,
                    madeCount = rInt,
                    status = null,
                    quest = QuestDto(
                        count = rInt,
                        type = QuestType.PublishVideo,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 0,
                    completed = false,
                    done = null,
                    madeCount = 0,
                    status = null,
                    quest = QuestDto(
                        count = 20,
                        type = QuestType.Watch,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 1,
                    completed = false,
                    done = null,
                    madeCount = 0,
                    status = null,
                    quest = QuestDto(
                        count = 5,
                        type = QuestType.Subscribe,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    energyProgress = 1,
                    completed = false,
                    done = null,
                    madeCount = rInt,
                    status = null,
                    quest = QuestDto(
                        count = rInt,
                        type = QuestType.SocialPost,
                        energy = 20,
                    )
                ),
            )
        ),
    )
}