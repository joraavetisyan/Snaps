package io.snaps.featurefeed.data

import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import io.snaps.featurefeed.data.model.CommentResponseDto
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