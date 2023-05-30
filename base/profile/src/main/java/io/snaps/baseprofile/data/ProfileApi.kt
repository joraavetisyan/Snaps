package io.snaps.baseprofile.data

import io.snaps.baseprofile.data.model.ConnectInstagramRequestDto
import io.snaps.baseprofile.data.model.SetInviteCodeRequestDto
import io.snaps.baseprofile.data.model.TransactionItemResponseDto
import io.snaps.baseprofile.data.model.UserCreateRequestDto
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.corecommon.model.Completable
import io.snaps.coredata.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ProfileApi {

    @GET("v1/user-info")
    suspend fun userInfo(
        @Query("userId") userId: String? = null,
    ): BaseResponse<UserInfoResponseDto>

    @POST("v1/user")
    suspend fun createUser(
        @Body body: UserCreateRequestDto,
    ): BaseResponse<UserInfoResponseDto>

    @POST("v1/invite-code")
    suspend fun setInviteCode(
        @Body body: SetInviteCodeRequestDto,
    ): BaseResponse<Completable>

    @GET("v1/user/balance/unlocked/history")
    suspend fun unlockedTransactions(
        @Query("from") from: String?,
        @Query("count") count: Int,
    ): BaseResponse<List<TransactionItemResponseDto>>

    @GET("v1/user/balance/locked/history")
    suspend fun lockedTransactions(
        @Query("from") from: String?,
        @Query("count") count: Int,
    ): BaseResponse<List<TransactionItemResponseDto>>

    @POST("v1/user")
    suspend fun connectInstagram(
        @Body body: ConnectInstagramRequestDto,
    ): BaseResponse<UserInfoResponseDto>

    @GET("v1/user")
    suspend fun users(
        @Query("searchString") query: String?,
        @Query("from") from: String?,
        @Query("count") count: Int,
        @Query("onlyInvited") onlyInvited: Boolean,
    ): BaseResponse<List<UserInfoResponseDto>>
}