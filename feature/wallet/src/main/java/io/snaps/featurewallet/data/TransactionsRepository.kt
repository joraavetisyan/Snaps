package io.snaps.featurewallet.data

import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.featurewallet.data.model.TransactionType
import io.snaps.featurewallet.domain.TransactionPageModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface TransactionsRepository {

    fun getTransactionsState(transactionType: TransactionType): StateFlow<TransactionPageModel>

    suspend fun refreshTransactions(transactionType: TransactionType): Effect<Completable>

    suspend fun loadNextTransactionsPage(transactionType: TransactionType): Effect<Completable>
}

class TransactionsRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val transactionsApi: TransactionsApi,
    private val loaderFactory: TransactionsLoaderFactory,
) : TransactionsRepository {

    private fun getLoader(transactionType: TransactionType): TransactionsLoader {
        return loaderFactory.get(transactionType) {
            PagedLoaderParams(
                action = { from, count ->
                    transactionsApi.transactions(from = from, count = count, transactionType = it)
                },
                pageSize = 20,
                mapper = { it.toModelList() }
            )
        }
    }

    override fun getTransactionsState(transactionType: TransactionType): StateFlow<TransactionPageModel> {
        return getLoader(transactionType).state
    }

    override suspend fun refreshTransactions(transactionType: TransactionType): Effect<Completable> {
        return getLoader(transactionType).refresh()
    }

    override suspend fun loadNextTransactionsPage(transactionType: TransactionType): Effect<Completable> {
        return getLoader(transactionType).loadNext()
    }
}