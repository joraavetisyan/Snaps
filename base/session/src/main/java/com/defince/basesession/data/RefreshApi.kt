package com.defince.basesession.data

import com.defince.basesession.data.model.RefreshRequestDto
import com.defince.basesession.data.model.RefreshResponseDto
import com.defince.coredata.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApi {

    @POST("v1/auth/token-refresh")
    suspend fun refresh(@Body body: RefreshRequestDto): BaseResponse<RefreshResponseDto>
}