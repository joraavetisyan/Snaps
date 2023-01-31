package com.defince.basesession.data

import com.defince.basesession.data.model.LogoutRequestDto
import com.defince.corecommon.model.Completable
import com.defince.coredata.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LogoutApi {

    @POST("v1/logout")
    suspend fun logout(@Body body: LogoutRequestDto): BaseResponse<Completable>
}