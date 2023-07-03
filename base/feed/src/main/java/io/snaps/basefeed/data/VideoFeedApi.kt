package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.AddVideoRequestDto
import io.snaps.basefeed.data.model.AddVideoResponseDto
import io.snaps.basefeed.data.model.LikedVideoFeedItemResponseDto
import io.snaps.basefeed.data.model.MarkVideoShownRequestDto
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

    @GET("v1/video-feed")
    suspend fun getFeed(
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>

    @GET("v1/user/video")
    suspend fun getMyFeed(
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>

    @GET("v1/user/{userId}/video")
    suspend fun getUserFeed(
        @Path("userId") userId: Uuid,
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>

    @GET("v1/popular-video-feed")
    suspend fun getPopularFeed(
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>

    @GET("v1/video/subscriptions")
    suspend fun getSubscriptionFeed(
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>

    @GET("v1/video")
    suspend fun getSearchFeed(
        @Query("searchString") query: String?,
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>

    @GET("v1/user/likes")
    suspend fun getMyLikedFeed(
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<LikedVideoFeedItemResponseDto>>

    @GET("v1/user/{userId}/likes")
    suspend fun getLikedFeed(
        @Path("userId") userId: Uuid?,
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>

    @POST("v1/video/{videoId}/like")
    suspend fun likeVideo(
        @Path("videoId") videoId: Uuid,
    ): BaseResponse<Completable>

    @POST("v1/video/{videoId}/showed")
    suspend fun markVideoShown(
        @Path("videoId") videoId: Uuid,
        @Body body: MarkVideoShownRequestDto
    ): BaseResponse<Completable>

    @POST("v1/video/{videoId}/view")
    suspend fun markVideoWatched(
        @Path("videoId") videoId: Uuid,
    ): BaseResponse<Completable>

    @GET("v1/video/{videoId}")
    suspend fun getVideo(
        @Path("videoId") videoId: Uuid,
    ): BaseResponse<VideoFeedItemResponseDto>

    @POST("v1/video")
    suspend fun addVideo(
        @Body body: AddVideoRequestDto,
    ): BaseResponse<AddVideoResponseDto>

    @Multipart
    @POST("v1/{videoId}/upload")
    suspend fun uploadVideo(
        @Part file: MultipartBody.Part,
        @Path("videoId") videoId: Uuid,
    ): BaseResponse<Completable>

    @DELETE("v1/video/{videoId}")
    suspend fun deleteVideo(
        @Path("videoId") videoId: Uuid,
    ): BaseResponse<Completable>
}