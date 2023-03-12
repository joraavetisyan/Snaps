package io.snaps.featurewallet.data

import io.snaps.corecommon.mock.mockDelay
import io.snaps.coredata.network.BaseResponse
import io.snaps.featurewallet.data.model.TransactionItemResponseDto
import io.snaps.featurewallet.data.model.TransactionType
import kotlinx.coroutines.delay

class FakeTransactionsApi : TransactionsApi {

    override suspend fun transactions(
        from: Int,
        count: Int,
        transactionType: TransactionType
    ): BaseResponse<List<TransactionItemResponseDto>> {
        return BaseResponse(
            actualTimestamp = 0L,
            data = getTransactions()
        ).also {
            delay(mockDelay)
        }
    }

    private fun getTransactions() = List(10) {
        TransactionItemResponseDto(
            id = it.toString(),
            symbol = "BNB",
            iconUrl = "https://baksman.org/res/exchangebox/uploads/networks/BNB.png",
            date = "2023-03-01T00:00:00+00:00",
            coinValue = "0.743",
        )
    }
}