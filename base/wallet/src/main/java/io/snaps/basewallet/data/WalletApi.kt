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
import io.snaps.corecommon.model.Completable
import io.snaps.coredata.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WalletApi {

    @GET("v1/user/balance")
    suspend fun getSnpsAccount(): BaseResponse<SnpsAccountResponseDto>

    @POST("v1/wallet")
    suspend fun save(
        @Body body: WalletSaveRequestDto,
    ): BaseResponse<Completable>

    @POST("v1/wallet/claim")
    suspend fun claim(
        @Body body: ClaimRequestDto,
    ): BaseResponse<ClaimResponseDto>

    @POST("v1/wallet/max")
    suspend fun claimMax(): BaseResponse<ClaimResponseDto>

    @POST("v1/payout-order")
    suspend fun payoutOrder(
        @Body body: PayoutOrderRequestDto,
    ): BaseResponse<Completable>

    @GET("v1/payout-order")
    suspend fun payoutStatus(): BaseResponse<List<PayoutOrderResponseDto>>

    @POST("v1/user/nft/sign-repair")
    suspend fun getRepairSignature(
        @Body body: SignatureRequestDto,
    ): BaseResponse<SignatureResponseDto>

    @POST("v1/user/nft/sign-mint")
    suspend fun getMintSignature(
        @Body body: SignatureRequestDto,
    ): BaseResponse<SignatureResponseDto>

    @POST("v1/user/mystery-box/sign")
    suspend fun getMysteryBoxSignature(
        @Body body: SignatureRequestDto,
    ): BaseResponse<MysteryBoxSignatureResponseDto>

    @POST("v2/base-token")
    suspend fun refillGas(
        @Body body: RefillGasRequestDto,
    ): BaseResponse<Completable>
}