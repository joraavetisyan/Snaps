package io.snaps.featureregistration.presentation.data

import io.snaps.corecommon.model.Token
import io.snaps.coredata.network.BaseResponse

class FakeAuthApi : AuthApi {

    override suspend fun auth(token: String): BaseResponse<Token> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = "Fake auth token",
        )
    }
}