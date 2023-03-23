package io.snaps.featurewallet.data

import io.snaps.baseprofile.data.ProfileApi
import io.snaps.baseprofile.data.model.TransactionType
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.PagedLoaderParams
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
    private val profileApi: ProfileApi,
    private val loaderFactory: TransactionsLoaderFactory,
) : TransactionsRepository {

    private fun getLoader(transactionType: TransactionType): TransactionsLoader {
        return loaderFactory.get(transactionType) {
            PagedLoaderParams(
                action = { from, count ->
                    profileApi.transactions(from = from, count = count, transactionType = it)
                },
                pageSize = 20,
                nextPageIdFactory = { it.id },
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