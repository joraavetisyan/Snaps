package io.snaps.basenft.data

import io.snaps.basenft.data.model.BundleItemResponseDto
import io.snaps.basenft.data.model.MintBundleResponseDto
import io.snaps.basenft.data.model.MintMysteryBoxRequestDto
import io.snaps.basenft.data.model.MintMysteryBoxResponseDto
import io.snaps.basenft.data.model.MintNftRequestDto
import io.snaps.basenft.data.model.MintNftResponseDto
import io.snaps.basenft.data.model.MintNftStoreRequestDto
import io.snaps.basenft.data.model.MysteryBoxItemResponseDto
import io.snaps.basenft.data.model.NftItemResponseDto
import io.snaps.basenft.data.model.RepairAllGlassesRequestDto
import io.snaps.basenft.data.model.RepairGlassesRequestDto
import io.snaps.basenft.data.model.RepairGlassesResponseDto
import io.snaps.basenft.data.model.UserNftItemResponseDto
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NftApi {

    @GET("v1/user/nft")
    suspend fun getCurrentUserNftCollection(): BaseResponse<List<UserNftItemResponseDto>>

    @GET("v1/nft")
    suspend fun getNfts(): BaseResponse<List<NftItemResponseDto>>

    @POST("v2/user/nft/mint/android")
    suspend fun mintNftStore(
        @Body body: MintNftStoreRequestDto,
    ): BaseResponse<MintNftResponseDto>

    @POST("v1/user/nft/mint")
    suspend fun mintNft(
        @Body body: MintNftRequestDto,
    ): BaseResponse<MintNftResponseDto>

    @POST("v1/user/mystery-box")
    suspend fun mintMysteryBox(
        @Body body: MintMysteryBoxRequestDto,
    ): BaseResponse<MintMysteryBoxResponseDto>

    @POST("v1/user/nft/repair")
    suspend fun repairGlasses(
        @Body body: RepairGlassesRequestDto,
    ): BaseResponse<RepairGlassesResponseDto>

    @POST("v1/user/nft/repair-all")
    suspend fun repairAllGlasses(
        @Body body: RepairAllGlassesRequestDto,
    ): BaseResponse<RepairGlassesResponseDto>

    @GET("v1/user/mystery-box")
    suspend fun getMysteryBoxes(): BaseResponse<List<MysteryBoxItemResponseDto>>

    @GET("v1/user/{userId}/nft")
    suspend fun getUserNftCollection(@Path("userId") userId: Uuid): BaseResponse<List<UserNftItemResponseDto>>

    @GET("v1/user/bundle")
    suspend fun getBundles(): BaseResponse<List<BundleItemResponseDto>>

    @POST("v1/user/bundle")
    suspend fun mintBundle(
        @Body body: MintMysteryBoxRequestDto,
    ): BaseResponse<MintBundleResponseDto>
}