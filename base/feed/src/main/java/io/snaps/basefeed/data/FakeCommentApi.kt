package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.CommentResponseDto
import io.snaps.basefeed.data.model.CreateCommentRequestDto
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rBool
import io.snaps.corecommon.mock.rImage
import io.snaps.corecommon.mock.rInt
import io.snaps.corecommon.model.Completable
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
                    videoId = "1",
                    ownerImage = rImage,
                    ownerName = "Owner of comment",
                    text = "Comment text",
                    likes = rInt,
                    isLiked = rBool,
                    createdDate = "2023-02-20T15:05:59.9105429+00:00",
                    isOwnerVerified = rBool,
                    ownerTitle = "Owner title",
                )
            }
        ).also { generation++ }
    }

    override suspend fun createComment(
        videoId: Uuid,
        body: CreateCommentRequestDto
    ): BaseResponse<Completable> {
        log("Requesting create comment")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = Completable,
        )
    }
}