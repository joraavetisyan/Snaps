package io.snaps.baseprofile.data

import io.snaps.baseprofile.data.model.SetInviteCodeRequestDto
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.corecommon.model.Completable
import io.snaps.coredata.network.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ProfileApi {

    @GET("user-info")
    suspend fun userInfo(
        @Query("userId") userId: String? = null,
    ): BaseResponse<UserInfoResponseDto>

    @Multipart
    @POST("user-info")
    suspend fun createUser(
        @Part file: MultipartBody.Part,
        @Part("userName") userName: RequestBody,
    ): BaseResponse<UserInfoResponseDto>

    @POST("invite-code")
    suspend fun setInviteCode(
        @Body body: SetInviteCodeRequestDto,
    ): BaseResponse<Completable>
}