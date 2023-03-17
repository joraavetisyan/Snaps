package io.snaps.featureprofile.data

import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import io.snaps.featureprofile.data.model.SubscribeRequestDto
import io.snaps.featureprofile.data.model.SubscriptionItemResponseDto
import io.snaps.featureprofile.data.model.UnsubscribeRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SubsApi {

    @GET("subscribes")
    suspend fun subscribers(
        @Query("userId") userId: Uuid? = null,
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<SubscriptionItemResponseDto>>

    @GET("subscriptions")
    suspend fun subscriptions(
        @Query("userId") userId: Uuid? = null,
        @Query("from") from: Uuid?,
        @Query("count") count: Int,
    ): BaseResponse<List<SubscriptionItemResponseDto>>

    @POST("subscribe")
    suspend fun subscribe(
        @Body body: SubscribeRequestDto
    ): BaseResponse<Completable>

    @POST("unsubscribe")
    suspend fun unsubscribe(
        @Body body: UnsubscribeRequestDto
    ): BaseResponse<Completable>
}