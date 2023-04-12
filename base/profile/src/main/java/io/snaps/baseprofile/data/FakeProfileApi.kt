package io.snaps.baseprofile.data

import io.snaps.baseprofile.data.model.BalanceResponseDto
import io.snaps.baseprofile.data.model.ConnectInstagramRequestDto
import io.snaps.baseprofile.data.model.QuestDto
import io.snaps.baseprofile.data.model.QuestInfoResponseDto
import io.snaps.baseprofile.data.model.QuestItemDto
import io.snaps.baseprofile.data.model.SetInviteCodeRequestDto
import io.snaps.baseprofile.data.model.TransactionItemResponseDto
import io.snaps.baseprofile.data.model.TransactionType
import io.snaps.baseprofile.data.model.UserCreateRequestDto
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rInt
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.QuestType
import io.snaps.coredata.network.BaseResponse
import kotlinx.coroutines.delay
import retrofit2.http.Body
import retrofit2.http.Query

class FakeProfileApi : ProfileApi {

    override suspend fun userInfo(userId: String?): BaseResponse<UserInfoResponseDto> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = getUserInfo(userId),
        )
    }

    override suspend fun createUser(@Body body: UserCreateRequestDto): BaseResponse<UserInfoResponseDto> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = getUserInfo(null),
        )
    }

    override suspend fun setInviteCode(body: SetInviteCodeRequestDto): BaseResponse<Completable> {
        delay(mockDelay)
        return BaseResponse(
            actualTimestamp = 0L,
            data = Completable,
        )
    }

    override suspend fun transactions(
        @Query(value = "from") from: String?,
        @Query(value = "count") count: Int,
    ): BaseResponse<List<TransactionItemResponseDto>> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = getTransactions()
        ).also {
            delay(mockDelay)
        }
    }

    override suspend fun balance(): BaseResponse<BalanceResponseDto> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = BalanceResponseDto(
                lockedTokensBalance = 1,
                unlockedTokensBalance = 1,
                exchangeRate = 342.01,
            )
        ).also {
            delay(mockDelay)
        }
    }

    override suspend fun connectInstagram(@Body body: ConnectInstagramRequestDto): BaseResponse<UserInfoResponseDto> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = getUserInfo(null)
        ).also {
            delay(mockDelay)
        }
    }

    override suspend fun users(
        query: String?,
        from: String?,
        count: Int
    ): BaseResponse<List<UserInfoResponseDto>> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = List(10) {
                getUserInfo("user $it")
            },
        )
    }

    private fun getTransactions() = List(10) {
        TransactionItemResponseDto(
            id = it.toString(),
            date = "2023-03-01T00:00:00+00:00",
            balanceChange = rInt,
            type = TransactionType.Withdrawal,
            userId = it.toString(),
        )
    }

    private fun getUserInfo(userId: String?) = UserInfoResponseDto(
        entityId = "63e1bb860007e5354351d549",
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
    )

    private fun getQuestInfo() = QuestInfoResponseDto(
        questDate = "2023-03-06T00:00:00+00:00",
        updatedDate = "2023-02-07T02:46:30.3218237+00:00",
        experience = 0,
        energy = 20,
        quests = listOf(
            QuestItemDto(
                energyProgress = 2,
                completed = true,
                done = null,
                madeCount = rInt,
                status = null,
                quest = QuestDto(
                    count = 20,
                    type = QuestType.Like,
                    energy = 20,
                )
            ),
            QuestItemDto(
                energyProgress = 2,
                done = null,
                completed = false,
                madeCount = rInt,
                status = null,
                quest = QuestDto(
                    count = rInt,
                    type = QuestType.PublishVideo,
                    energy = 20,
                )
            ),
            QuestItemDto(
                energyProgress = 0,
                completed = false,
                done = null,
                madeCount = 0,
                status = null,
                quest = QuestDto(
                    count = 20,
                    type = QuestType.Watch,
                    energy = 20,
                )
            ),
            QuestItemDto(
                energyProgress = 1,
                completed = false,
                done = null,
                madeCount = 0,
                status = null,
                quest = QuestDto(
                    count = 5,
                    type = QuestType.Subscribe,
                    energy = 20,
                )
            ),
            QuestItemDto(
                energyProgress = 1,
                completed = false,
                done = null,
                madeCount = rInt,
                status = null,
                quest = QuestDto(
                    count = rInt,
                    type = QuestType.SocialPost,
                    energy = 20,
                )
            ),
        )
    )
}