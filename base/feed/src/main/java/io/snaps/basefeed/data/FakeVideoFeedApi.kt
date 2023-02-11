package io.snaps.basefeed.data

import io.snaps.corecommon.ext.log
import io.snaps.coredata.network.BaseResponse
import io.snaps.basefeed.data.model.VideoFeedItemResponseDto
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val delay = 2000L

private val rInt get() = Random.nextInt(from = 0, until = 200000)

class FakeVideoFeedApi : VideoFeedApi {
    private val urls = listOf(
        "https://user-images.githubusercontent.com/90382113/170887700-e405c71e-fe31-458d-8572-aea2e801eecc.mp4",
        "https://user-images.githubusercontent.com/90382113/170885742-d82e3b59-e45a-4fcf-a851-fed58ff5a690.mp4",
        "https://user-images.githubusercontent.com/90382113/170888784-5d9a7623-10c8-4ca2-8585-fdb29b2ed037.mp4",
        "https://user-images.githubusercontent.com/90382113/170889265-7ed9a56c-dd5f-4d78-b453-18b011644da0.mp4",
        "https://user-images.githubusercontent.com/90382113/170890384-43214cc8-79c6-4815-bcb7-e22f6f7fe1bc.mp4",
    )

    private var generation = 0
    private var popularGeneration = 0

    override suspend fun feed(from: Int, count: Int): BaseResponse<List<VideoFeedItemResponseDto>> {
        log("Requesting feed: $count videos with offset $from")
        delay(delay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = List(count) {
                VideoFeedItemResponseDto(
                    url = urls.random(),
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
        delay(delay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = List(count) {
                VideoFeedItemResponseDto(
                    url = urls.random(),
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