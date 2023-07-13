package io.snaps.basenotifications.data

import io.snaps.basenotifications.data.model.NotificationItemResponseDto
import io.snaps.coredata.network.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NotificationsApi {

    @GET("v1/user-action-history")
    suspend fun getNotifications(
        @Query("skip") from: Int,
        @Query("count") count: Int,
    ): BaseResponse<List<NotificationItemResponseDto>>
}