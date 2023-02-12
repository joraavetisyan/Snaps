package io.snaps.featureregistration.presentation.data

import io.snaps.corecommon.model.Token
import io.snaps.coredata.network.BaseResponse

interface AuthApi {

    suspend fun auth(token: String): BaseResponse<Token>
}