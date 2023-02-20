package io.snaps.featurecollection.data

import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.BaseResponse
import io.snaps.featurecollection.data.model.NftResponseDto
import io.snaps.featurecollection.data.model.RankItemResponseDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MyCollectionApi {

    @GET("nft")
    suspend fun nftCollection(): BaseResponse<NftResponseDto>

    @GET("mystery-box")
    suspend fun mysteryBoxCollection(): BaseResponse<NftResponseDto>

    @GET("rank")
    suspend fun ranks(): BaseResponse<List<RankItemResponseDto>>

    @POST("nft/{rankId}")
    suspend fun addNft(
        @Path("rankId") rankId: Uuid,
    ): BaseResponse<Completable>
}