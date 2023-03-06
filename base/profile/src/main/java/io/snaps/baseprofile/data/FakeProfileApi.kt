package io.snaps.baseprofile.data

import io.snaps.baseprofile.data.model.QuestDto
import io.snaps.baseprofile.data.model.QuestInfoResponseDto
import io.snaps.baseprofile.data.model.QuestItemDto
import io.snaps.baseprofile.data.model.SetInviteCodeRequestDto
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.mock.rBool
import io.snaps.corecommon.mock.rInt
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.QuestType
import io.snaps.coredata.network.BaseResponse
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeProfileApi : ProfileApi {

    override suspend fun userInfo(userId: String?): BaseResponse<UserInfoResponseDto> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = getUserInfo(userId),
        )
    }

    override suspend fun createUser(
        file: MultipartBody.Part,
        userName: RequestBody
    ): BaseResponse<UserInfoResponseDto> {
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

    private fun getUserInfo(userId: String?) = UserInfoResponseDto(
        entityId = "63e1bb860007e5354351d549",
        createdDate = "2023-02-07T02:46:30.3218237+00:00",
        userId = "101939668681812837937",
        email = "pozdnyshevmaksim@gmail.com",
        wallet = "63e1bb860007e5354351d549",
        name = "Вадим",
        totalLikes = 4,
        avatarUrl = "https://lh3.googleusercontent.com/a/AEdFTp5fj_vYT-nRYQ9RXjKbZniPZoLGlZ0ViZ9pX-ij5A=s96-c",
        totalPublication = 23,
        totalSubscribers = 12,
        totalSubscriptions = 10,
        hasNft = rBool,
        experience = 0,
        level = 1,
        questInfo = getQuestInfo(),
        ownInviteCode = "#42GJXE8QM",
        inviteCodeRegisteredBy = null,
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
                madeCount = rInt,
                network = null,
                quest = QuestDto(
                    count = 20,
                    type = QuestType.Like,
                    energy = 20,
                )
            ),
            QuestItemDto(
                energyProgress = 2,
                completed = false,
                madeCount = rInt,
                network = null,
                quest = QuestDto(
                    count = rInt,
                    type = QuestType.PublishVideo,
                    energy = 20,
                )
            ),
            QuestItemDto(
                energyProgress = 0,
                completed = false,
                madeCount = 0,
                network = null,
                quest = QuestDto(
                    count = 20,
                    type = QuestType.Watch,
                    energy = 20,
                )
            ),
            QuestItemDto(
                energyProgress = 1,
                completed = false,
                madeCount = 0,
                network = null,
                quest = QuestDto(
                    count = 5,
                    type = QuestType.Subscribe,
                    energy = 20,
                )
            ),
            QuestItemDto(
                energyProgress = 1,
                completed = false,
                madeCount = rInt,
                network = null,
                quest = QuestDto(
                    count = rInt,
                    type = QuestType.SocialPost,
                    energy = 20,
                )
            ),
        )
    )
}