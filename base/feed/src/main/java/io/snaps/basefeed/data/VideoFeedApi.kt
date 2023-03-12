package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.AddVideoRequestDto
import io.snaps.basefeed.data.model.CreateCommentRequestDto
import io.snaps.basefeed.data.model.ShareInfoRequestDto
import io.snaps.coredata.network.BaseResponse
import io.snaps.basefeed.data.model.VideoFeedItemResponseDto
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Uuid
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface VideoFeedApi {

    // todo api cannot accept from, throws, so null for now
    @GET("video-feed")
    suspend fun feed(
        @Query("from") from: Int?,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>

    // todo api cannot accept from, throws, so null for now
    @GET("popular-video-feed")
    suspend fun popularFeed(
        @Query("from") from: Int?,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>

    // todo there must not be from and count
    @GET("video/likes")
    suspend fun likedVideos(
        @Query("from") from: Int?,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>

    @POST("video/{videoId}/like")
    suspend fun like(
        @Path("videoId") videoId: Uuid,
    ): BaseResponse<Completable>

    @POST("video/{videoId}/view")
    suspend fun view(
        @Path("videoId") videoId: Uuid,
    ): BaseResponse<Completable>

    @POST("video")
    suspend fun addVideo(
        @Body body: AddVideoRequestDto,
    ): BaseResponse<VideoFeedItemResponseDto>

    @POST("share-info")
    suspend fun shareInfo(
        @Body body: ShareInfoRequestDto,
    ): BaseResponse<Completable>
}