package io.snaps.featureprofile.data

import io.snaps.baseprofile.data.model.PaymentsState
import io.snaps.baseprofile.data.model.QuestInfoResponseDto
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rBool
import io.snaps.corecommon.mock.rImage
import io.snaps.corecommon.mock.rInt
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import io.snaps.featureprofile.data.model.SubscribeRequestDto
import io.snaps.featureprofile.data.model.SubscriptionItemResponseDto
import io.snaps.featureprofile.data.model.UnsubscribeRequestDto
import kotlinx.coroutines.delay
import retrofit2.http.Query

class FakeSubsApi : SubsApi {

    private var subscriberGeneration = 0
    private var subscriptionGeneration = 0

    override suspend fun subscribers(
        @Query(value = "userId") userId: Uuid?,
        @Query(value = "from") from: Uuid?,
        @Query(value = "count") count: Int,
    ): BaseResponse<List<SubscriptionItemResponseDto>> {
        log("Requesting subscribers: $count subscribers with offset $from")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = List(count) {
                SubscriptionItemResponseDto(
                    userId = "${subscriberGeneration}subscriber$it",
                    imageUrl = rImage,
                    name = "$it subscriber",
                    isSubscribed = rBool,
                )
            }
        ).also { subscriberGeneration++ }
    }

    override suspend fun subscriptions(
        @Query(value = "userId") userId: Uuid?,
        @Query(value = "from") from: Uuid?,
        @Query(value = "count") count: Int,
    ): BaseResponse<List<SubscriptionItemResponseDto>> {
        log("Requesting subscriptions: $count subscriptions with offset $from")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = List(count) {
                SubscriptionItemResponseDto(
                    userId = "${subscriptionGeneration}subscription$it",
                    imageUrl = rImage,
                    name = "$it subscriptions",
                    isSubscribed = rBool,
                )
            }
        ).also { subscriptionGeneration++ }
    }

    override suspend fun subscriptions(
        from: Uuid?,
        count: Int
    ): BaseResponse<List<UserInfoResponseDto>> {
        log("Requesting subscribers: $count subscribers with offset $from")
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 1L,
            data = List(count) {
                UserInfoResponseDto(
                    entityId = "${subscriberGeneration}entity$it",
                    userId = "${subscriberGeneration}subscriber$it",
                    email = "$it email",
                    wallet = "$it wallet",
                    createdDate = "",
                    avatarUrl = rImage,
                    name = "$it subscriber",
                    experience = 0,
                    level = 1,
                    instagramId = null,
                    ownInviteCode = null,
                    inviteCodeRegisteredBy = null,
                    questInfo = QuestInfoResponseDto(
                        quests = emptyList(),
                        questDate = "",
                        roundEndDate = "",
                        experience = 0,
                        energy = 20,
                        roundId = "",
                        id = "",
                    ),
                    totalLikes = rInt,
                    totalSubscribers = rInt,
                    totalSubscriptions = rInt,
                    paymentsState = PaymentsState.No,
                )
            }
        ).also { subscriberGeneration++ }
    }

    override suspend fun subscribe(body: SubscribeRequestDto): BaseResponse<Completable> {
        delay(mockDelay)
        log("Requesting subscribe: ${body.toSubscribeUserId}")
        return BaseResponse(
            actualTimestamp = 1L,
            data = Completable,
        )
    }

    override suspend fun unsubscribe(body: UnsubscribeRequestDto): BaseResponse<Completable> {
        delay(mockDelay)
        log("Requesting unsubscribe: ${body.subscriptionId}")
        return BaseResponse(
            actualTimestamp = 1L,
            data = Completable,
        )
    }
}