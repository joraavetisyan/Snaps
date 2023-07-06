package io.snaps.basequests.data

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.snaps.basequests.data.model.QuestItemResponseDto
import io.snaps.basequests.domain.QuestModel
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.coroutine.UserSessionCoroutineScope
import io.snaps.coredata.network.Action
import io.snaps.coredata.network.PagedLoader
import io.snaps.coredata.network.PagedLoaderFactory
import io.snaps.coredata.network.PagedLoaderParams
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

class HistoryQuestsLoader @AssistedInject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @UserSessionCoroutineScope private val scope: CoroutineScope,
    action: Action,
    @Assisted private val params: PagedLoaderParams<QuestItemResponseDto, QuestModel>,
) : PagedLoader<QuestItemResponseDto, QuestModel>(
    ioDispatcher = ioDispatcher,
    scope = scope,
    action = action,
    params = params,
)

@AssistedFactory
abstract class HistoryQuestsLoaderFactory :
    PagedLoaderFactory<Unit, HistoryQuestsLoader, QuestItemResponseDto, QuestModel>() {

    override fun provide(params: PagedLoaderParams<QuestItemResponseDto, QuestModel>) = create(params)

    abstract fun create(params: PagedLoaderParams<QuestItemResponseDto, QuestModel>): HistoryQuestsLoader
}