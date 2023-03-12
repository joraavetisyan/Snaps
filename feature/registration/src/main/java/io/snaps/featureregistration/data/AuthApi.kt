package io.snaps.featureregistration.data

import io.snaps.corecommon.model.Token
import io.snaps.coredata.network.BaseResponse

interface AuthApi {

    // todo
    suspend fun auth(token: String): BaseResponse<Token>
}