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

    fun getTransactionsState(transactionsType: TransactionsType): StateFlow<TransactionPageModel>

    suspend fun refreshTransactions(transactionsType: TransactionsType): Effect<Completable>

    suspend fun loadNextTransactionsPage(transactionsType: TransactionsType): Effect<Completable>
}

class TransactionsRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val profileApi: ProfileApi,
    private val loaderFactory: TransactionsLoaderFactory,
) : TransactionsRepository {

    private fun getLoader(transactionsType: TransactionsType): TransactionsLoader {
        return loaderFactory.get(transactionsType) { type ->
            when (type) {
                TransactionsType.Unlocked -> PagedLoaderParams(
                    action = { from, count ->
                        profileApi.unlockedTransactions(from = from, count = count)
                    },
                    pageSize = 12,
                    nextPageIdFactory = { it.id },
                    mapper = { it.toModelList() }
                )
                TransactionsType.Locked -> PagedLoaderParams(
                    action = { from, count ->
                        profileApi.lockedTransactions(from = from, count = count)
                    },
                    pageSize = 12,
                    nextPageIdFactory = { it.id },
                    mapper = { it.toModelList() }
                )
            }
        }
    }

    override fun getTransactionsState(transactionsType: TransactionsType): StateFlow<TransactionPageModel> {
        return getLoader(transactionsType).state
    }

    override suspend fun refreshTransactions(transactionsType: TransactionsType): Effect<Completable> {
        return getLoader(transactionsType).refresh()
    }

    override suspend fun loadNextTransactionsPage(transactionsType: TransactionsType): Effect<Completable> {
        return getLoader(transactionsType).loadNext()
    }
}