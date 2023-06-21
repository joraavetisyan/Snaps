package io.snaps.basewallet.data

import io.snaps.basewallet.data.model.SnpsAccountResponseDto
import io.snaps.basewallet.data.model.ClaimRequestDto
import io.snaps.basewallet.data.model.ClaimResponseDto
import io.snaps.basewallet.data.model.MysteryBoxSignatureResponseDto
import io.snaps.basewallet.data.model.PayoutOrderRequestDto
import io.snaps.basewallet.data.model.PayoutOrderResponseDto
import io.snaps.basewallet.data.model.RefillGasRequestDto
import io.snaps.basewallet.data.model.SignatureRequestDto
import io.snaps.basewallet.data.model.SignatureResponseDto
import io.snaps.basewallet.data.model.WalletSaveRequestDto
import io.snaps.corecommon.mock.mockDelay
import io.snaps.corecommon.model.Completable
import io.snaps.coredata.network.BaseResponse
import kotlinx.coroutines.delay

class FakeWalletApi : WalletApi {

    override suspend fun getSnpsAccount(): BaseResponse<SnpsAccountResponseDto> {
        return BaseResponse(
            data = SnpsAccountResponseDto(
                lockedTokensBalance = 1.0,
                unlockedTokensBalance = 1.0,
                snpsExchangeRate = 342.01,
                bnbExchangeRate = 342.01,
            )
        ).also {
            delay(mockDelay)
        }
    }

    override suspend fun save(body: WalletSaveRequestDto): BaseResponse<Completable> {
        return BaseResponse(Completable)
    }

    override suspend fun claim(body: ClaimRequestDto): BaseResponse<ClaimResponseDto> {
        return BaseResponse(ClaimResponseDto(0.1))
    }

    override suspend fun claimMax(): BaseResponse<ClaimResponseDto> {
        return BaseResponse(ClaimResponseDto(0.1))
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

    override suspend fun getMysteryBoxSignature(body: SignatureRequestDto): BaseResponse<MysteryBoxSignatureResponseDto> {
        return BaseResponse(
            MysteryBoxSignatureResponseDto(
                signature = "",
                deadline = 0,
                amountReceiver = "",
                profitWallet = "",
                tokensCount = 0,
            )
        )
    }

    override suspend fun refillGas(body: RefillGasRequestDto): BaseResponse<Completable> {
        return BaseResponse(Completable)
    }
}