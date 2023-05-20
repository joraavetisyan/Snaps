package io.snaps.basewallet.data

import io.snaps.basewallet.data.model.BalanceResponseDto
import io.snaps.basewallet.data.model.ClaimRequestDto
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

    @GET("user/balance")
    suspend fun balance(): BaseResponse<BalanceResponseDto>

    @POST("wallet")
    suspend fun save(
        @Body body: WalletSaveRequestDto,
    ): BaseResponse<Completable>

    @POST("wallet/claim")
    suspend fun claim(
        @Body body: ClaimRequestDto,
    ): BaseResponse<Completable>

    @POST("payout-order")
    suspend fun payoutOrder(
        @Body body: PayoutOrderRequestDto,
    ): BaseResponse<Completable>

    @GET("payout-order")
    suspend fun payoutStatus(): BaseResponse<List<PayoutOrderResponseDto>>

    @POST("user/nft/sign-repair")
    suspend fun getRepairSignature(
        @Body body: SignatureRequestDto,
    ): BaseResponse<SignatureResponseDto>

    @POST("user/nft/sign-mint")
    suspend fun getMintSignature(
        @Body body: SignatureRequestDto,
    ): BaseResponse<SignatureResponseDto>

    @POST("base-token")
    suspend fun refillGas(
        @Body body: RefillGasRequestDto,
    ): BaseResponse<Completable>
}