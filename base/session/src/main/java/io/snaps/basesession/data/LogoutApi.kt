package io.snaps.basesession.data

import io.snaps.basesession.data.model.LogoutRequestDto
import io.snaps.corecommon.model.Completable
import io.snaps.coredata.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LogoutApi {

    @POST("v1/logout")
    suspend fun logout(@Body body: LogoutRequestDto): BaseResponse<Completable>
}