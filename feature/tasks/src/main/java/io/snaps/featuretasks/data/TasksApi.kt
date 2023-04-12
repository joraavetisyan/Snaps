package io.snaps.featuretasks.data

import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.DateTime
import io.snaps.coredata.network.BaseResponse
import io.snaps.featuretasks.data.model.HistoryTaskItemResponseDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TasksApi {

    @GET("quest-history")
    suspend fun historyTasks(
        @Query("from") from: DateTime?,
        @Query("count") count: Int,
    ): BaseResponse<List<HistoryTaskItemResponseDto>>

    @POST("quests/instagram-post")
    suspend fun instagramPost(): BaseResponse<Completable>
}