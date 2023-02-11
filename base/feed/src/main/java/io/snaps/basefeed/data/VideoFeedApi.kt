package io.snaps.basefeed.data

import io.snaps.coredata.network.BaseResponse
import io.snaps.basefeed.data.model.VideoFeedItemResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoFeedApi {

    @GET("video-feed")
    suspend fun feed(
        @Query("from") from: Int,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>

    @GET("popular-video-feed")
    suspend fun popularFeed(
        @Query("from") from: Int,
        @Query("count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>>
}