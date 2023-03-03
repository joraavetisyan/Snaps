package io.snaps.featuretasks.data

import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import io.snaps.featuretasks.data.model.TaskItemResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TasksApi {

    @GET("tasks/history")
    suspend fun historyTasks(): BaseResponse<List<TaskItemResponseDto>>

    @GET("task")
    suspend fun task(
        @Query("taskId") taskId: Uuid,
    ): BaseResponse<TaskItemResponseDto>
}