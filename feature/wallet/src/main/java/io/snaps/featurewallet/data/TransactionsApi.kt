package io.snaps.featurewallet.data

import io.snaps.coredata.network.BaseResponse
import io.snaps.featurewallet.data.model.BalanceResponseDto
import io.snaps.featurewallet.data.model.TransactionItemResponseDto
import io.snaps.featurewallet.data.model.TransactionType
import retrofit2.http.GET
import retrofit2.http.Query

interface TransactionsApi {

    @GET("transactions")
    suspend fun transactions(
        @Query("from") from: Int,
        @Query("count") count: Int,
        @Query("transactionType") transactionType: TransactionType,
    ): BaseResponse<List<TransactionItemResponseDto>>

    @GET("user/balance")
    suspend fun balance(): BaseResponse<BalanceResponseDto>
}