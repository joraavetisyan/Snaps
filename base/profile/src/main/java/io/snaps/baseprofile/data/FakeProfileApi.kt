package io.snaps.baseprofile.data

import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.coredata.network.BaseResponse

class FakeProfileApi : ProfileApi {

    override suspend fun userInfo(userId: String?): BaseResponse<UserInfoResponseDto> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = getUserInfo(userId),
        )
    }

    private fun getUserInfo(userId: String?) = UserInfoResponseDto(
        entityId = "63e1bb860007e5354351d549",
        createdDate = "2023-02-07T02:46:30.3218237+00:00",
        userId = "101939668681812837937",
        email = "pozdnyshevmaksim@gmail.com",
        wallet = "63e1bb860007e5354351d549",
        name = "Вадим",
        totalLikes = "4",
        avatarUrl = "https://lh3.googleusercontent.com/a/AEdFTp5fj_vYT-nRYQ9RXjKbZniPZoLGlZ0ViZ9pX-ij5A=s96-c",
        totalPublication = "23",
        totalSubscribers = "12",
        totalSubscriptions = "10",
    )
}