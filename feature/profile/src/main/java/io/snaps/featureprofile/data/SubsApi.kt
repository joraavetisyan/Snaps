package io.snaps.featureprofile.data

import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import io.snaps.featureprofile.data.model.SubscriptionItemResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SubsApi {

    @GET("subscribes")
    suspend fun subscribers(
        @Query("userId") userId: Uuid? = null,
        @Query("from") from: Int,
        @Query("count") count: Int,
    ): BaseResponse<List<SubscriptionItemResponseDto>>

    @GET("subscriptions")
    suspend fun subscriptions(
        @Query("userId") userId: Uuid? = null,
        @Query("from") from: Int,
        @Query("count") count: Int,
    ): BaseResponse<List<SubscriptionItemResponseDto>>
}