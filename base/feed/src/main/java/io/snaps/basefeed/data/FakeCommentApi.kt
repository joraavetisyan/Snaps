package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.CommentResponseDto
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rBool
import io.snaps.corecommon.mock.rImage
import io.snaps.corecommon.mock.rInt
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import kotlinx.coroutines.delay
import retrofit2.http.Query

class FakeCommentApi : CommentApi {

    private var generation = 0

    override suspend fun comments(
        @Query(value = "from") from: Int,
        @Query(value = "count") count: Int,
        @Query(value = "videoId") videoId: Uuid
    ): BaseResponse<List<CommentResponseDto>> {
        log("Requesting feed: $count videos with offset $from")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = List(count) {
                CommentResponseDto(
                    id = "${generation}comment$it",
                    ownerImage = rImage,
                    ownerName = "Owner of comment",
                    text = "Comment text",
                    likes = rInt,
                    isLiked = rBool,
                    time = "22:22",
                    isOwnerVerified = rBool,
                    ownerTitle = "Owner title",
                )
            }
        ).also { generation++ }
    }
}