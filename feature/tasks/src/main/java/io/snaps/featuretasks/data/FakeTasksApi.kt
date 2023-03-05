package io.snaps.featuretasks.data

import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import io.snaps.featuretasks.data.model.HistoryTaskItemResponseDto
import kotlinx.coroutines.delay
import retrofit2.http.Query

class FakeTasksApi : TasksApi {

    override suspend fun historyTasks(
        @Query(value = "from") from: Int,
        @Query(value = "count") count: Int
    ): BaseResponse<List<HistoryTaskItemResponseDto>> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = getTasks()
        ).also {
            delay(mockDelay)
        }
    }

    override suspend fun task(taskId: Uuid): BaseResponse<HistoryTaskItemResponseDto> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = getTasks().find { it.id == taskId }
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
            date = "2023-03-01T00:00:00+00:00"
        ),
        HistoryTaskItemResponseDto(
            id = "2",
            userId = "2",
            energy = 20,
            experience = 0,
            date = "2023-03-01T00:00:00+00:00"
        ),
        HistoryTaskItemResponseDto(
            id = "3",
            userId = "3",
            energy = 20,
            experience = 0,
            date = "2023-03-01T00:00:00+00:00"
        ),
        HistoryTaskItemResponseDto(
            id = "4",
            userId = "4",
            energy = 20,
            experience = 0,
            date = "2023-03-01T00:00:00+00:00"
        ),
    )
}