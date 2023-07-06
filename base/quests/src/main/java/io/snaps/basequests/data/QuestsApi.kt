package io.snaps.basequests.data

import io.snaps.basequests.data.model.QuestItemResponseDto
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.DateTime
import io.snaps.coredata.network.BaseResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface QuestsApi {

    @GET("v1/quest-history")
    suspend fun historyQuests(
        @Query("from") from: DateTime?,
        @Query("count") count: Int,
    ): BaseResponse<List<QuestItemResponseDto>>

    @GET("v1/quests")
    suspend fun currentQuests(): BaseResponse<QuestItemResponseDto>

    @POST("v1/quests/instagram-post")
    suspend fun instagramPost(): BaseResponse<Completable>
}