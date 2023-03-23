package io.snaps.featurewallet.data

import io.snaps.baseprofile.data.ProfileApi
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.featurewallet.domain.TransactionPageModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface TransactionsRepository {

    fun getTransactionsState(): StateFlow<TransactionPageModel>

    suspend fun refreshTransactions(): Effect<Completable>

    suspend fun loadNextTransactionsPage(): Effect<Completable>
}

class TransactionsRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val profileApi: ProfileApi,
    private val loaderFactory: TransactionsLoaderFactory,
) : TransactionsRepository {

    private fun getLoader(): TransactionsLoader {
        return loaderFactory.get(Unit) {
            PagedLoaderParams(
                action = { from, count ->
                    profileApi.transactions(from = from, count = count)
                },
                pageSize = 20,
                nextPageIdFactory = { it.id },
                mapper = { it.toModelList() }
            )
        }
    }

    override fun getTransactionsState(): StateFlow<TransactionPageModel> {
        return getLoader().state
    }

    override suspend fun refreshTransactions(): Effect<Completable> {
        return getLoader().refresh()
    }

    override suspend fun loadNextTransactionsPage(): Effect<Completable> {
        return getLoader().loadNext()
    }
}