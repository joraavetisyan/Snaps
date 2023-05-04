package io.snaps.basewallet.data

import io.snaps.basewallet.data.model.ClaimRequestDto
import io.snaps.basewallet.data.model.NftSignatureRequestDto
import io.snaps.basewallet.data.model.NftSignatureResponseDto
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

    override suspend fun getRepairSignature(body: NftSignatureRequestDto): BaseResponse<NftSignatureResponseDto> {
        return BaseResponse(
            1L, NftSignatureResponseDto(
                signature = "",
                deadline = 0,
                amountReceiver = "",
                profitWallet = "",
                contract = "",
            )
        )
    }

    override suspend fun getMintSignature(body: NftSignatureRequestDto): BaseResponse<NftSignatureResponseDto> {
        return BaseResponse(
            1L, NftSignatureResponseDto(
                signature = "",
                deadline = 0,
                amountReceiver = "",
                profitWallet = "",
                contract = "",
            )
        )
    }
}