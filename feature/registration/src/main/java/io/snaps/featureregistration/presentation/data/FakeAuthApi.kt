package io.snaps.featureregistration.presentation.data

import io.snaps.corecommon.model.Completable
import io.snaps.coredata.network.BaseResponse

class FakeAuthApi : AuthApi {

    override suspend fun auth(token: String): BaseResponse<Completable> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = Completable,
        )
    }
}