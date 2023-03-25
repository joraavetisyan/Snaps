package io.snaps.basenft.data

import io.snaps.basenft.data.model.MintNftRequestDto
import io.snaps.basenft.data.model.MintNftResponseDto
import io.snaps.basenft.data.model.NftItemResponseDto
import io.snaps.basenft.data.model.RepairGlassesRequestDto
import io.snaps.basenft.data.model.UserNftItemResponseDto
import io.snaps.corecommon.model.Completable
import io.snaps.coredata.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NftApi {

    @GET("user/nft")
    suspend fun userNftCollection(): BaseResponse<List<UserNftItemResponseDto>>

    @GET("nft")
    suspend fun nft(): BaseResponse<List<NftItemResponseDto>>

    @POST("user/nft/mint")
    suspend fun mintNft(
        @Body body: MintNftRequestDto,
    ): BaseResponse<MintNftResponseDto>

    @POST("user/nft/repair")
    suspend fun repairGlasses(
        @Body body: RepairGlassesRequestDto,
    ): BaseResponse<Completable>
}