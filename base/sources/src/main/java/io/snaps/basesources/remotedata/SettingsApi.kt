package io.snaps.basesources.remotedata

import io.snaps.basesources.remotedata.model.SettingsDto
import io.snaps.coredata.network.BaseResponse
import retrofit2.http.GET

interface SettingsApi {

    @GET("v1/mobile-settings")
    suspend fun getSettings(): BaseResponse<SettingsDto>
}