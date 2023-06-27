package io.snaps.baseprofile.data

import io.snaps.baseprofile.data.model.ConnectInstagramRequestDto
import io.snaps.baseprofile.data.model.EditUserRequestDto
import io.snaps.baseprofile.data.model.InvitedReferralResponseDto
import io.snaps.baseprofile.data.model.PaymentsState
import io.snaps.baseprofile.data.model.QuestDto
import io.snaps.baseprofile.data.model.QuestInfoResponseDto
import io.snaps.baseprofile.data.model.QuestItemDto
import io.snaps.baseprofile.data.model.SetInviteCodeRequestDto
import io.snaps.baseprofile.data.model.TransactionItemResponseDto
import io.snaps.baseprofile.data.model.TransactionType
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rDouble
import io.snaps.corecommon.mock.rInt
import io.snaps.corecommon.model.TaskType
import io.snaps.coredata.network.BaseResponse
import kotlinx.coroutines.delay
import retrofit2.http.Body
import retrofit2.http.Query

class FakeProfileApi : ProfileApi {

    override suspend fun userInfo(userId: String?): BaseResponse<UserInfoResponseDto> {
        return BaseResponse(
            data = getUserInfo(userId),
        )
    }

    override suspend fun editUser(@Body body: EditUserRequestDto): BaseResponse<UserInfoResponseDto> {
        return BaseResponse(
            data = getUserInfo(null),
        )
    }

    override suspend fun setInviteCode(body: SetInviteCodeRequestDto): BaseResponse<UserInfoResponseDto> {
        delay(mockDelay)
        return BaseResponse(
            data = getUserInfo(null),
        )
    }

    override suspend fun unlockedTransactions(
        from: String?,
        count: Int
    ): BaseResponse<List<TransactionItemResponseDto>> {
        return BaseResponse(
            data = getTransactions()
        ).also {
            delay(mockDelay)
        }
    }

    override suspend fun lockedTransactions(
        from: String?,
        count: Int
    ): BaseResponse<List<TransactionItemResponseDto>> {
        return BaseResponse(
            data = getTransactions()
        ).also {
            delay(mockDelay)
        }
    }

    override suspend fun connectInstagram(@Body body: ConnectInstagramRequestDto): BaseResponse<UserInfoResponseDto> {
        return BaseResponse(
            data = getUserInfo(null)
        ).also {
            delay(mockDelay)
        }
    }

    override suspend fun users(
        @Query(value = "searchString") query: String?,
        @Query(value = "from") from: String?,
        @Query(value = "count") count: Int,
        @Query(value = "onlyInvited") onlyInvited: Boolean,
    ): BaseResponse<List<UserInfoResponseDto>> {
        return BaseResponse(
            data = List(10) {
                getUserInfo("user $it")
            },
        )
    }

    override suspend fun getInvitedFirstReferral(): BaseResponse<InvitedReferralResponseDto> {
        return BaseResponse(
            data = InvitedReferralResponseDto(
                users = listOf(getUserInfo(null)),
                total = rInt,
            ),
        )
    }

    override suspend fun getInvitedSecondReferral(): BaseResponse<InvitedReferralResponseDto> {
        return BaseResponse(
            data = InvitedReferralResponseDto(
                users = listOf(getUserInfo(null)),
                total = rInt,
            ),
        )
    }

    private fun getTransactions() = List(10) {
        TransactionItemResponseDto(
            id = it.toString(),
            date = "2023-03-01T00:00:00+00:00",
            balanceChange = rDouble,
            type = TransactionType.Withdrawal,
            userId = it.toString(),
        )
    }

    companion object {

        fun getUserInfo(userId: String?) = UserInfoResponseDto(
            entityId = userId ?: "63e1bb860007e5354351d549",
            createdDate = "2023-02-07T02:46:30.3218237+00:00",
            userId = "101939668681812837937",
            email = "pozdnyshevmaksim@gmail.com",
            wallet = "63e1bb860007e5354351d549",
            name = "Вадим",
            totalLikes = 4,
            avatarUrl = "https://lh3.googleusercontent.com/a/AEdFTp5fj_vYT-nRYQ9RXjKbZniPZoLGlZ0ViZ9pX-ij5A=s96-c",
            totalSubscribers = 12,
            totalSubscriptions = 10,
            experience = 0,
            level = 1,
            questInfo = getQuestInfo(),
            ownInviteCode = "#42GJXE8QM",
            inviteCodeRegisteredBy = null,
            instagramId = null,
            paymentsState = PaymentsState.No,
            firstLevelReferralMultiplier = 0.03,
            secondLevelReferralMultiplier = 0.01,
        )

        fun getQuestInfo() = QuestInfoResponseDto(
            questDate = "2023-03-06T00:00:00+00:00",
            roundEndDate = "2023-02-07T02:46:30.3218237+00:00",
            experience = 0,
            energy = 20,
            roundId = "1",
            id = "",
            quests = listOf(
                QuestItemDto(
                    completed = true,
                    done = null,
                    madeCount = rInt,
                    status = null,
                    quest = QuestDto(
                        count = 20,
                        type = TaskType.Like,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    done = null,
                    completed = false,
                    madeCount = rInt,
                    status = null,
                    quest = QuestDto(
                        count = rInt,
                        type = TaskType.PublishVideo,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    completed = false,
                    done = null,
                    madeCount = 0,
                    status = null,
                    quest = QuestDto(
                        count = 20,
                        type = TaskType.Watch,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    completed = false,
                    done = null,
                    madeCount = 0,
                    status = null,
                    quest = QuestDto(
                        count = 5,
                        type = TaskType.Subscribe,
                        energy = 20,
                    )
                ),
                QuestItemDto(
                    completed = false,
                    done = null,
                    madeCount = rInt,
                    status = null,
                    quest = QuestDto(
                        count = rInt,
                        type = TaskType.SocialPost,
                        energy = 20,
                    )
                ),
            )
        )
    }
}