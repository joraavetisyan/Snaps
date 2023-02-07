package io.snaps.baseprofile.data

import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.coredata.network.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfileApi {

    @GET("user-info")
    suspend fun userInfo(
        @Query("userId") userId: String? = null,
    ): BaseResponse<UserInfoResponseDto>
}