package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.AddVideoRequestDto
import io.snaps.basefeed.data.model.ShareInfoRequestDto
import io.snaps.basefeed.data.model.VideoFeedItemResponseDto
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rVideos
import io.snaps.corecommon.mock.rInt
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import kotlinx.coroutines.delay

class FakeVideoFeedApi : VideoFeedApi {

    private var generation = 0
    private var popularGeneration = 0

    override suspend fun feed(from: Int, count: Int): BaseResponse<List<VideoFeedItemResponseDto>> {
        log("Requesting feed: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = List(count) {
                VideoFeedItemResponseDto(
                    url = rVideos.random(),
                    internalId = "${popularGeneration}video$it",
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
            }
        ).also { generation++ }
    }

    override suspend fun popularFeed(
        from: Int,
        count: Int
    ): BaseResponse<List<VideoFeedItemResponseDto>> {
        log("Requesting popular feed: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = List(count) {
                VideoFeedItemResponseDto(
                    url = rVideos.random(),
                    entityId = "${popularGeneration}video$it",
                    internalId = "${popularGeneration}video$it",
                    createdDate = "",
                    viewsCount = rInt,
                    commentsCount = rInt,
                    likesCount = rInt,
                    title = "title $it",
                    description = "description $it",
                    authorUserId = "authorUserId$it",
                    thumbnailUrl = "https://picsum.photos/177/222",
                )
            }
        ).also { popularGeneration++ }
    }

    override suspend fun like(videoId: Uuid): BaseResponse<Completable> {
        log("Requesting like video")
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

    override suspend fun shareInfo(body: ShareInfoRequestDto): BaseResponse<Completable> {
        log("Requesting share video")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = Completable,
        )
    }
}