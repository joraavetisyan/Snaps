package io.snaps.basewallet.data

import io.snaps.basewallet.data.model.ClaimRequestDto
import io.snaps.basewallet.data.model.PayoutOrderRequestDto
import io.snaps.basewallet.data.model.PayoutOrderResponseDto
import io.snaps.basewallet.data.model.SignatureRequestDto
import io.snaps.basewallet.data.model.SignatureResponseDto
import io.snaps.basewallet.data.model.WalletSaveRequestDto
import io.snaps.corecommon.model.Completable
import io.snaps.coredata.network.BaseResponse

class FakeWalletApi : WalletApi {

    override suspend fun save(body: WalletSaveRequestDto): BaseResponse<Completable> {
        return BaseResponse(Completable)
    }

    override suspend fun claim(body: ClaimRequestDto): BaseResponse<Completable> {
        return BaseResponse(Completable)
    }

    override suspend fun payoutStatus(): BaseResponse<List<PayoutOrderResponseDto>> {
        return BaseResponse(listOf())
    }

    override suspend fun payoutOrder(body: PayoutOrderRequestDto): BaseResponse<Completable> {
        return BaseResponse(Completable)
    }

    override suspend fun getRepairSignature(body: SignatureRequestDto): BaseResponse<SignatureResponseDto> {
        return BaseResponse(
            SignatureResponseDto(
                signature = "",
                deadline = 0,
                amountReceiver = "",
                profitWallet = "",
                contract = "",
            )
        )
    }

    override suspend fun getMintSignature(body: SignatureRequestDto): BaseResponse<SignatureResponseDto> {
        return BaseResponse(
            SignatureResponseDto(
                signature = "",
                deadline = 0,
                amountReceiver = "",
                profitWallet = "",
                contract = "",
            )
        )
    }
}