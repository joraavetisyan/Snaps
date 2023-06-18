package io.snaps.featurewallet.data

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.Action
import io.snaps.coredata.network.PagedLoader
import io.snaps.coredata.network.PagedLoaderFactory
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.baseprofile.data.model.TransactionItemResponseDto
import io.snaps.coredata.coroutine.UserSessionCoroutineScope
import io.snaps.featurewallet.domain.TransactionModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

sealed interface TransactionsType {

    object Unlocked : TransactionsType

    object Locked : TransactionsType
}

class TransactionsLoader @AssistedInject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @UserSessionCoroutineScope private val scope: CoroutineScope,
    action: Action,
    @Assisted private val params: PagedLoaderParams<TransactionItemResponseDto, TransactionModel>,
) : PagedLoader<TransactionItemResponseDto, TransactionModel>(
    ioDispatcher = ioDispatcher,
    scope = scope,
    action = action,
    params = params,
)

@AssistedFactory
abstract class TransactionsLoaderFactory :
    PagedLoaderFactory<TransactionsType, TransactionsLoader, TransactionItemResponseDto, TransactionModel>() {

    override fun provide(params: PagedLoaderParams<TransactionItemResponseDto, TransactionModel>) = create(params)

    abstract fun create(params: PagedLoaderParams<TransactionItemResponseDto, TransactionModel>): TransactionsLoader
}