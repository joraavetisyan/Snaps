package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.AddVideoRequestDto
import io.snaps.basefeed.data.model.UserLikedVideoResponseDto
import io.snaps.basefeed.data.model.VideoFeedItemResponseDto
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface VideoFeedApi {

    @GET("video-feed")
    suspend fun feed(
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>

    @GET("user/{userId}/video")
    suspend fun userFeed(
        @Path("userId") userId: Uuid,
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>

    @GET("user/video")
    suspend fun myFeed(
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>

    @GET("popular-video-feed")
    suspend fun popularFeed(
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>

    // todo there must not be from and count
    @GET("user/likes")
    suspend fun likedVideos(
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<UserLikedVideoResponseDto>>

    @GET("video")
    suspend fun videos(
        @Query("searchString") query: String?,
        @Query("from") from: Uuid?,
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

    @Multipart
    @POST("{videoId}/upload")
    suspend fun uploadVideo(
        @Part file: MultipartBody.Part,
        @Path("videoId") videoId: Uuid,
    ): BaseResponse<Completable>

    @DELETE("video/{videoId}")
    suspend fun deleteVideo(
        @Path("videoId") videoId: Uuid,
    ): BaseResponse<Completable> // delete user video
}