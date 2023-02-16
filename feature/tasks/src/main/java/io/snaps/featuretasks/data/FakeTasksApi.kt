package io.snaps.featuretasks.data

import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import io.snaps.featuretasks.data.model.TaskItemResponseDto
import io.snaps.featuretasks.data.model.TaskType
import kotlinx.coroutines.delay

class FakeTasksApi : TasksApi {

    override suspend fun historyTasks(): BaseResponse<List<TaskItemResponseDto>> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = getTasks()
        ).also {
            delay(mockDelay)
        }
    }

    override suspend fun currentTasks(): BaseResponse<List<TaskItemResponseDto>> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = getTasks()
        ).also {
            delay(mockDelay)
        }
    }

    override suspend fun task(taskId: Uuid): BaseResponse<TaskItemResponseDto> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = getTasks().find { it.id == taskId }
        ).also {
            delay(mockDelay)
        }
    }

    private fun getTasks() = listOf(
        TaskItemResponseDto(
            id = "1",
            title = "Watch the video feed",
            description = "View at least 50 videos with a retention of at least 70 percent",
            type = TaskType.WatchVideo,
            energy = 15,
            energyProgress = 15,
            done = true,
            count = 100,
            madeCount = 100,
        ),
        TaskItemResponseDto(
            id = "2",
            title = "Like and subscribe to the users you like",
            description = "At least 10 likes, at least 5 subscriptions = 10 energy pointsView at least 50 videos with a retention of at least 70 percent",
            type = TaskType.LikeAndSubscribe,
            energy = 15,
            energyProgress = 2,
            done = false,
            count = 100,
            madeCount = 42,
        ),
        TaskItemResponseDto(
            id = "3",
            title = "Socialize to earn",
            description = "Post a post/story to Instagram with a template generated inside the app.",
            type = TaskType.Share,
            energy = 15,
            energyProgress = 2,
            done = false,
            count = null,
            madeCount = null,
        ),
        TaskItemResponseDto(
            id = "4",
            description = "The minimum video length is 5 seconds, the maximum video length is 1 minute = 15 energy points.",
            title = "Post video to your profile",
            type = TaskType.FindPoints,
            energy = 15,
            energyProgress = 0,
            done = false,
            count = null,
            madeCount = null,
        ),
    )
}