package io.snaps.featurecollection.data

import io.snaps.coredata.network.BaseResponse
import io.snaps.featurecollection.data.model.NftResponseDto
import io.snaps.featurecollection.data.model.RankItemResponseDto
import retrofit2.http.GET

interface MyCollectionApi {

    @GET("nft")
    suspend fun nftCollection(): BaseResponse<NftResponseDto>

    @GET("mystery-box")
    suspend fun mysteryBoxCollection(): BaseResponse<NftResponseDto>

    @GET("rank")
    suspend fun ranks(): BaseResponse<List<RankItemResponseDto>>
}