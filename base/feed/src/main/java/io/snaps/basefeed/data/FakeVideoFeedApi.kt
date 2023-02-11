package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.VideoFeedItemResponseDto
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rVideos
import io.snaps.corecommon.mock.rInt
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
                    entityId = "${generation}video$it",
                    createdDate = "",
                    viewsCount = rInt,
                    commentsCount = rInt,
                    likesCount = rInt,
                    title = "title $it",
                    description = "description $it",
                    authorUserId = "authorUserId$it",
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
                    createdDate = "",
                    viewsCount = rInt,
                    commentsCount = rInt,
                    likesCount = rInt,
                    title = "title $it",
                    description = "description $it",
                    authorUserId = "authorUserId$it",
                )
            }
        ).also { popularGeneration++ }
    }
}