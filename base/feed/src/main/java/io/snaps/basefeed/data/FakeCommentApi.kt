package io.snaps.basefeed.data

import io.snaps.basefeed.data.model.CommentResponseDto
import io.snaps.basefeed.data.model.CreateCommentRequestDto
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rInt
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import kotlinx.coroutines.delay
import retrofit2.http.Path
import retrofit2.http.Query

class FakeCommentApi : CommentApi {

    private var generation = 0

    override suspend fun comments(
        @Path(value = "videoId") videoId: Uuid,
        @Query(value = "from") from: Uuid?,
        @Query(value = "count") count: Int,
    ): BaseResponse<List<CommentResponseDto>> {
        log("Requesting comments: $count comments with offset $from")
        delay(mockDelay)
        return BaseResponse(
            data = List(count) {
                CommentResponseDto(
                    id = "${generation}comment$it",
                    videoId = "1",
                    text = "Comment text",
                    createdDate = "2023-02-20T15:05:59.9105429+00:00",
                    userId = "userId$rInt",
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
            data = Completable,
        )
    }
}