package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.AddVideoRequestDto
import io.snaps.basefeed.data.model.AddVideoResponseDto
import io.snaps.basefeed.data.model.LikedVideoFeedItemResponseDto
import io.snaps.basefeed.data.model.VideoFeedItemResponseDto
import io.snaps.basefeed.data.model.VideoStatus
import io.snaps.baseprofile.data.FakeProfileApi
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rInt
import io.snaps.corecommon.mock.rVideos
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import retrofit2.http.Path
import retrofit2.http.Query

class FakeVideoFeedApi : VideoFeedApi {

    private var generation = 0
    private var popularGeneration = 0
    private var myGeneration = 0
    private var userGeneration = 0

    override suspend fun getFeed(
        @Query(value = "from") from: Uuid?,
        @Query(value = "count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>> {
        log("Requesting feed: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            data = List(count) { videoFeedItemResponseDto(it, generation++) }
        )
    }

    private fun videoFeedItemResponseDto(i: Int, generation: Int) = VideoFeedItemResponseDto(
        url = rVideos.random(),
        internalId = "${generation}video$i",
        entityId = "${generation}video$i",
        createdDate = "",
        viewsCount = rInt,
        commentsCount = rInt,
        likesCount = rInt,
        title = "title $i",
        description = "description $i",
        author = FakeProfileApi.getUserInfo("authorUserId$i"),
        authorId = "authorUserId$i",
        thumbnailUrl = "https://picsum.photos/177/222",
        status = VideoStatus.Rejected,
        isLiked = false,
    )

    override suspend fun getMyFeed(
        from: Uuid?,
        count: Int
    ): BaseResponse<List<VideoFeedItemResponseDto>> {
        log("Requesting my feed: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            data = List(count) { videoFeedItemResponseDto(it, myGeneration++) }
        )
    }

    override suspend fun getUserFeed(
        userId: Uuid,
        from: Uuid?,
        count: Int
    ): BaseResponse<List<VideoFeedItemResponseDto>> {
        log("Requesting user $userId feed: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            data = List(count) { videoFeedItemResponseDto(it, userGeneration++) }
        )
    }

    override suspend fun getPopularFeed(
        @Query(value = "from") from: Uuid?,
        @Query(value = "count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>> {
        log("Requesting popular feed: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            data = List(count) { videoFeedItemResponseDto(it, popularGeneration++) }
        )
    }

    override suspend fun getSubscriptionFeed(
        from: Uuid?,
        count: Int
    ): BaseResponse<List<VideoFeedItemResponseDto>> {
        return BaseResponse(
            data = List(count) { videoFeedItemResponseDto(it, generation++) }
        )
    }

    override suspend fun getMyLikedFeed(
        @Query(value = "from") from: Uuid?,
        @Query(value = "count") count: Int,
    ): BaseResponse<List<LikedVideoFeedItemResponseDto>> {
        log("Requesting liked videos: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            data = List(count) {
                LikedVideoFeedItemResponseDto(video = videoFeedItemResponseDto(it, generation++))
            }
        )
    }

    override suspend fun getLikedFeed(
        @Path(value = "userId") userId: Uuid?,
        @Query(value = "from") from: Uuid?,
        @Query(value = "count") count: Int
    ): BaseResponse<List<VideoFeedItemResponseDto>> {
        log("Requesting liked videos: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            data = List(count) { videoFeedItemResponseDto(it, generation++) }
        )
    }

    override suspend fun getSearchFeed(
        query: String?,
        from: Uuid?,
        count: Int
    ): BaseResponse<List<VideoFeedItemResponseDto>> {
        log("Requesting video: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            data = List(count) { videoFeedItemResponseDto(it, generation++) }
        )
    }

    override suspend fun markVideoWatched(videoId: Uuid): BaseResponse<Completable> {
        log("Requesting view video $videoId")
        delay(mockDelay)
        return BaseResponse(
            data = Completable,
        )
    }

    override suspend fun likeVideo(videoId: Uuid): BaseResponse<Completable> {
        log("Requesting like video $videoId")
        delay(mockDelay)
        return BaseResponse(
            data = Completable,
        )
    }

    override suspend fun addVideo(body: AddVideoRequestDto): BaseResponse<AddVideoResponseDto> {
        log("Requesting add video")
        delay(mockDelay)
        return BaseResponse(data = AddVideoResponseDto(internalId = "${popularGeneration}video"))
    }

    override suspend fun uploadVideo(
        file: MultipartBody.Part,
        videoId: Uuid
    ): BaseResponse<Completable> {
        log("Requesting upload video")
        delay(mockDelay)
        return BaseResponse(
            data = Completable,
        )
    }

    override suspend fun deleteVideo(videoId: Uuid): BaseResponse<Completable> {
        log("Requesting delete video")
        delay(mockDelay)
        return BaseResponse(
            data = Completable,
        )
    }

    override suspend fun markVideoShown(videoId: Uuid): BaseResponse<Completable> {
        log("Requesting show video $videoId")
        delay(mockDelay)
        return BaseResponse(
            data = Completable,
        )
    }

    override suspend fun getVideo(videoId: Uuid): BaseResponse<VideoFeedItemResponseDto> {
        log("Requesting get video $videoId")
        delay(mockDelay)
        return BaseResponse(
            data = videoFeedItemResponseDto(1, generation),
        )
    }
}