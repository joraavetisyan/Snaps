package io.snaps.basesession.data

import io.snaps.basesession.data.model.RefreshRequestDto
import io.snaps.basesession.data.model.RefreshResponseDto
import io.snaps.coredata.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApi {

    @POST("v1/auth/token-refresh")
    suspend fun refresh(@Body body: RefreshRequestDto): BaseResponse<RefreshResponseDto>
}