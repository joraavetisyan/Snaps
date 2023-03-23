package io.snaps.featurecollection.data

import io.snaps.coredata.network.BaseResponse
import io.snaps.featurecollection.data.model.MintNftRequestDto
import io.snaps.featurecollection.data.model.MintNftResponseDto
import io.snaps.featurecollection.data.model.NftItemResponseDto
import io.snaps.featurecollection.data.model.UserNftItemResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MyCollectionApi {

    @GET("user/nft")
    suspend fun userNftCollection(): BaseResponse<List<UserNftItemResponseDto>>

    @GET("mystery-box")
    suspend fun mysteryBoxCollection(): BaseResponse<List<UserNftItemResponseDto>> // todo

    @GET("nft")
    suspend fun nft(): BaseResponse<List<NftItemResponseDto>>

    @POST("user/nft/mint")
    suspend fun mintNft(
        @Body body: MintNftRequestDto,
    ): BaseResponse<MintNftResponseDto>
}