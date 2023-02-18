package io.snaps.basewallet.data

import io.snaps.basewallet.data.model.WalletSaveRequestDto
import io.snaps.corecommon.model.Completable
import io.snaps.coredata.network.BaseResponse

class FakeWalletApi : WalletApi {

    override suspend fun save(body: WalletSaveRequestDto): BaseResponse<Completable> {
        return BaseResponse(1L, Completable)
    }
}