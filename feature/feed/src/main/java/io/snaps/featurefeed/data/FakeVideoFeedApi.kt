package io.snaps.featurefeed.data

import io.snaps.corecommon.ext.log
import io.snaps.coredata.network.BaseResponse
import io.snaps.featurefeed.data.model.VideoFeedItemResponseDto

class FakeVideoFeedApi : VideoFeedApi {

    private var generation = 0

    override suspend fun feed(from: Int, count: Int): BaseResponse<List<VideoFeedItemResponseDto>> {
        log("Requesting $count videos with offset $from")
        return BaseResponse(
            actualTimestamp = 1L,
            data = List(count) {
                VideoFeedItemResponseDto(
                    url = "https://user-images.githubusercontent.com/90382113/170887700-e405c71e-fe31-458d-8572-aea2e801eecc.mp4",
                    entityId = "${generation}video$it",
                    createdDate = "",
                    viewsCount = 9 * it,
                    commentsCount = 6 * it,
                    likesCount = 8 * it,
                    title = "title $it",
                    description = "description $it",
                    authorUserId = "authorUserId$it",
                )
            }
        ).also { generation++ }
    }
}