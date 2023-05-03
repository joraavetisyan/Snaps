package io.snaps.basewallet.data

import io.snaps.basewallet.data.model.ClaimRequestDto
import io.snaps.basewallet.data.model.NftRepairSignatureRequestDto
import io.snaps.basewallet.data.model.NftRepairSignatureResponseDto
import io.snaps.basewallet.data.model.WalletSaveRequestDto
import io.snaps.corecommon.model.Completable
import io.snaps.coredata.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface WalletApi {

    @POST("wallet")
    suspend fun save(
        @Body body: WalletSaveRequestDto,
    ): BaseResponse<Completable>

    @POST("wallet/claim")
    suspend fun claim(
        @Body body: ClaimRequestDto,
    ): BaseResponse<Completable>

    @POST("user/nft/sign-repair")
    suspend fun nftRepairSignature(
        @Body body: NftRepairSignatureRequestDto,
    ): BaseResponse<NftRepairSignatureResponseDto>
}