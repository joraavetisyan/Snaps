package io.snaps.basesubs.data

import io.snaps.corecommon.ext.log
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rBool
import io.snaps.corecommon.mock.rImage
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import io.snaps.basesubs.data.model.SubscribeRequestDto
import io.snaps.basesubs.data.model.SubsItemResponseDto
import io.snaps.basesubs.data.model.UnsubscribeRequestDto
import kotlinx.coroutines.delay
import retrofit2.http.Path
import retrofit2.http.Query

class FakeSubsApi : SubsApi {

    private var subscriberGeneration = 0
    private var subscriptionGeneration = 0

    override suspend fun subscribers(
        @Path(value = "userId") userId: Uuid,
        @Query(value = "from") from: Uuid?,
        @Query(value = "count") count: Int
    ): BaseResponse<List<SubsItemResponseDto>> {
        log("Requesting subscribers: $count subscribers with offset $from")
        delay(mockDelay)
        return BaseResponse(
            data = List(count) {
                SubsItemResponseDto(
                    entityId = "${subscriberGeneration}subscriber$it",
                    userId = "${subscriberGeneration}subscriber$it",
                    avatar = rImage,
                    name = "$it subscriber",
                )
            }
        ).also { subscriberGeneration++ }
    }

    override suspend fun subscriptions(
        @Path(value = "userId") userId: Uuid,
        @Query(value = "from") from: Uuid?,
        @Query(value = "count") count: Int
    ): BaseResponse<List<SubsItemResponseDto>> {
        log("Requesting subscriptions: $count subscriptions with offset $from")
        delay(mockDelay)
        return BaseResponse(
            data = List(count) {
                SubsItemResponseDto(
                    entityId = "${subscriptionGeneration}subscription$it",
                    userId = "${subscriptionGeneration}subscription$it",
                    avatar = rImage,
                    name = "$it subscriptions",
                )
            }
        ).also { subscriptionGeneration++ }
    }

    override suspend fun mySubscribers(from: Uuid?, count: Int): BaseResponse<List<SubsItemResponseDto>> {
        log("Requesting my subscribers: $count subscribers with offset $from")
        delay(mockDelay)
        return BaseResponse(
            data = List(count) {
                SubsItemResponseDto(
                    entityId = "${subscriptionGeneration}subscriber$it",
                    userId = "${subscriptionGeneration}subscriber$it",
                    avatar = rImage,
                    name = "$it subscriber",
                )
            }
        ).also { subscriptionGeneration++ }
    }

    override suspend fun mySubscriptions(from: Uuid?, count: Int): BaseResponse<List<SubsItemResponseDto>> {
        log("Requesting my subscriptions: $count subscriptions with offset $from")
        delay(mockDelay)
        return BaseResponse(
            data = List(count) {
                SubsItemResponseDto(
                    entityId = "${subscriptionGeneration}subscription$it",
                    userId = "${subscriptionGeneration}subscription$it",
                    avatar = rImage,
                    name = "$it subscriptions",
                )
            }
        ).also { subscriptionGeneration++ }
    }

    override suspend fun subscribe(body: SubscribeRequestDto): BaseResponse<Completable> {
        delay(mockDelay)
        log("Requesting subscribe: ${body.toSubscribeUserId}")
        return BaseResponse(
            data = Completable,
        )
    }

    override suspend fun unsubscribe(body: UnsubscribeRequestDto): BaseResponse<Completable> {
        delay(mockDelay)
        log("Requesting unsubscribe: ${body.subscriptionId}")
        return BaseResponse(
            data = Completable,
        )
    }
}