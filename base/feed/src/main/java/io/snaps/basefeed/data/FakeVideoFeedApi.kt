package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.AddVideoRequestDto
import io.snaps.basefeed.data.model.ShareInfoRequestDto
import io.snaps.basefeed.data.model.UserLikedVideoFeedItemResponseDto
import io.snaps.basefeed.data.model.VideoFeedItemResponseDto
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rInt
import io.snaps.corecommon.mock.rVideos
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import retrofit2.http.Query

class FakeVideoFeedApi : VideoFeedApi {

    private var generation = 0
    private var popularGeneration = 0
    private var myGeneration = 0
    private var userGeneration = 0

    override suspend fun feed(
        @Query(value = "from") from: Uuid?,
        @Query(value = "count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>> {
        log("Requesting feed: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = List(count) { videoFeedItemResponseDto(it, generation) }
        ).also { generation++ }
    }

    private fun videoFeedItemResponseDto(it: Int, generation: Int) = VideoFeedItemResponseDto(
        url = rVideos.random(),
        internalId = "${generation}video$it",
        entityId = "${generation}video$it",
        createdDate = "",
        viewsCount = rInt,
        commentsCount = rInt,
        likesCount = rInt,
        title = "title $it",
        description = "description $it",
        authorUserId = "authorUserId$it",
        thumbnailUrl = "https://picsum.photos/177/222",
    )

    override suspend fun myFeed(
        from: Uuid?,
        count: Int
    ): BaseResponse<List<VideoFeedItemResponseDto>> {
        log("Requesting my feed: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = List(count) { videoFeedItemResponseDto(it, myGeneration) }
        ).also { myGeneration++ }
    }

    override suspend fun userFeed(
        userId: Uuid,
        from: Uuid?,
        count: Int
    ): BaseResponse<List<VideoFeedItemResponseDto>> {
        log("Requesting user $userId feed: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = List(count) { videoFeedItemResponseDto(it, userGeneration) }
        ).also { userGeneration++ }
    }

    override suspend fun popularFeed(
        @Query(value = "from") from: Uuid?,
        @Query(value = "count") count: Int,
    ): BaseResponse<List<VideoFeedItemResponseDto>> {
        log("Requesting popular feed: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = List(count) { videoFeedItemResponseDto(it, popularGeneration) }
        ).also { popularGeneration++ }
    }

    override suspend fun likedVideos(
        @Query(value = "from") from: Uuid?,
        @Query(value = "count") count: Int,
    ): BaseResponse<List<UserLikedVideoFeedItemResponseDto>> {
        log("Requesting liked videos: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = List(count) {
                UserLikedVideoFeedItemResponseDto(
                    entityId = "${generation}video$it",
                    video = videoFeedItemResponseDto(it, 0)
                )
            }
        )
    }

    override suspend fun videos(
        searchString: String?,
        from: Uuid?,
        count: Int
    ): BaseResponse<List<VideoFeedItemResponseDto>> {
        log("Requesting video: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = List(count) { videoFeedItemResponseDto(it, generation) }
        ).also { generation++ }
    }

    override suspend fun view(videoId: Uuid): BaseResponse<Completable> {
        log("Requesting view video $videoId")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = Completable,
        )
    }

    override suspend fun like(videoId: Uuid): BaseResponse<Completable> {
        log("Requesting like video $videoId")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = Completable,
        )
    }

    override suspend fun addVideo(body: AddVideoRequestDto): BaseResponse<VideoFeedItemResponseDto> {
        log("Requesting add video")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = VideoFeedItemResponseDto(
                url = rVideos.random(),
                entityId = "${popularGeneration}video",
                internalId = "${popularGeneration}video",
                createdDate = "",
                viewsCount = rInt,
                commentsCount = rInt,
                likesCount = rInt,
                title = "title",
                description = "description",
                authorUserId = "authorUserId",
                thumbnailUrl = "https://picsum.photos/177/222",
            ),
        )
    }

    override suspend fun uploadVideo(
        file: MultipartBody.Part,
        videoId: Uuid
    ): BaseResponse<Completable> {
        log("Requesting upload video")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = Completable,
        )
    }

    override suspend fun shareInfo(body: ShareInfoRequestDto): BaseResponse<Completable> {
        log("Requesting share video")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = Completable,
        )
    }
}