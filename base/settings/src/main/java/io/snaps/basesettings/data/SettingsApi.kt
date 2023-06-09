package io.snaps.basesettings.data

import io.snaps.basesettings.data.model.CommonSettingsResponseDto
import io.snaps.basesources.remotedata.model.SettingsDto
import io.snaps.coredata.network.BaseResponse
import retrofit2.http.GET

interface SettingsApi {

    @GET("v1/mobile-settings")
    suspend fun settings(): BaseResponse<SettingsDto>

    @GET("v1/common-settings")
    suspend fun commonSettings(): BaseResponse<CommonSettingsResponseDto>
}