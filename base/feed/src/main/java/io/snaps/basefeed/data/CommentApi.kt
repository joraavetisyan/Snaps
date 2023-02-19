package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.CommentResponseDto
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CommentApi {

    @GET("video-feed")
    suspend fun comments(
        @Query("from") from: Int,
        @Query("count") count: Int,
        @Query("videoId") videoId: Uuid,
    ): BaseResponse<List<CommentResponseDto>>
}