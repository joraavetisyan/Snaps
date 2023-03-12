package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.CommentResponseDto
import io.snaps.basefeed.data.model.CreateCommentRequestDto
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentApi {

    // todo api cannot accept from, throws, so null for now
    @GET("video/{videoId}/comments")
    suspend fun comments(
        @Path("videoId") videoId: Uuid,
        @Query("from") from: Int?,
        @Query("count") count: Int,
    ): BaseResponse<List<CommentResponseDto>>

    @POST("video/{videoId}/comment")
    suspend fun createComment(
        @Path("videoId") videoId: Uuid,
        @Body body: CreateCommentRequestDto,
    ): BaseResponse<Completable>
}