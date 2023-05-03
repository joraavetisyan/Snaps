package io.snaps.basewallet.data

import io.snaps.basewallet.data.model.ClaimRequestDto
import io.snaps.basewallet.data.model.NftRepairSignatureRequestDto
import io.snaps.basewallet.data.model.NftRepairSignatureResponseDto
import io.snaps.basewallet.data.model.WalletSaveRequestDto
import io.snaps.corecommon.model.Completable
import io.snaps.coredata.network.BaseResponse

class FakeWalletApi : WalletApi {

    override suspend fun save(body: WalletSaveRequestDto): BaseResponse<Completable> {
        return BaseResponse(1L, Completable)
    }

    override suspend fun claim(body: ClaimRequestDto): BaseResponse<Completable> {
        return BaseResponse(1L, Completable)
    }

    override suspend fun nftRepairSignature(body: NftRepairSignatureRequestDto): BaseResponse<NftRepairSignatureResponseDto> {
        return BaseResponse(
            1L, NftRepairSignatureResponseDto(
                signature = "",
                deadline = 0,
                amountReceiver = "",
                profitWallet = "",
                contract = "",
            )
        )
    }
}