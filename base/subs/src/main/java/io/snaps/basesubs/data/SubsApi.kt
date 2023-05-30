package io.snaps.basesubs.data

import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import io.snaps.basesubs.data.model.SubscribeRequestDto
import io.snaps.basesubs.data.model.SubsItemResponseDto
import io.snaps.basesubs.data.model.UnsubscribeRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SubsApi {

    @GET("v1/user/{userId}/subscribers")
    suspend fun subscribers(
        @Path("userId") userId: Uuid,
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<SubsItemResponseDto>>

    @GET("v1/user/{userId}/subscriptions")
    suspend fun subscriptions(
        @Path("userId") userId: Uuid,
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<SubsItemResponseDto>>

    @GET("v1/subscribers")
    suspend fun mySubscribers(
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<SubsItemResponseDto>>

    @GET("v1/subscriptions")
    suspend fun mySubscriptions(
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<SubsItemResponseDto>>

    @POST("v1/subscribe")
    suspend fun subscribe(
        @Body body: SubscribeRequestDto
    ): BaseResponse<Completable>

    @POST("v1/unsubscribe")
    suspend fun unsubscribe(
        @Body body: UnsubscribeRequestDto
    ): BaseResponse<Completable>
}