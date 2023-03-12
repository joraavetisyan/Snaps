package io.snaps.featuretasks.data

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.Action
import io.snaps.coredata.network.PagedLoader
import io.snaps.coredata.network.PagedLoaderFactory
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.featuretasks.data.model.HistoryTaskItemResponseDto
import io.snaps.featuretasks.domain.TaskModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

class HistoryTasksLoader @AssistedInject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    action: Action,
    @Assisted private val params: PagedLoaderParams<HistoryTaskItemResponseDto, TaskModel>,
) : PagedLoader<HistoryTaskItemResponseDto, TaskModel>(
    ioDispatcher = ioDispatcher,
    scope = scope,
    action = action,
    params = params,
)

@AssistedFactory
abstract class HistoryTasksLoaderFactory :
    PagedLoaderFactory<Unit, HistoryTasksLoader, HistoryTaskItemResponseDto, TaskModel>() {

    override fun provide(params: PagedLoaderParams<HistoryTaskItemResponseDto, TaskModel>) = create(params)

    abstract fun create(params: PagedLoaderParams<HistoryTaskItemResponseDto, TaskModel>): HistoryTasksLoader
}