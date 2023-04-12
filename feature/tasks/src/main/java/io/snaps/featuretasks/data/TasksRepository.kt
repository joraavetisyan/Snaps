package io.snaps.featuretasks.data

import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.coredata.network.apiCall
import io.snaps.featuretasks.data.model.HistoryTaskItemResponseDto
import io.snaps.featuretasks.domain.TaskPageModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface TasksRepository {

    fun getHistoryTasksState(): StateFlow<TaskPageModel>

    suspend fun refreshHistoryTasks(): Effect<Completable>

    suspend fun loadNextHistoryTaskPage(): Effect<Completable>

    suspend fun postToInstagram(): Effect<Completable>
}

class TasksRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val tasksApi: TasksApi,
    private val loaderFactory: HistoryTasksLoaderFactory,
) : TasksRepository {

    private fun getLoader(): HistoryTasksLoader {
        return loaderFactory.get(Unit) {
            PagedLoaderParams(
                action = { from, count -> tasksApi.historyTasks(from = from, count = count) },
                pageSize = 20,
                nextPageIdFactory = { it.date },
                mapper = List<HistoryTaskItemResponseDto>::toModelList,
            )
        }
    }

    override fun getHistoryTasksState(): StateFlow<TaskPageModel> = getLoader().state

    override suspend fun refreshHistoryTasks(): Effect<Completable> = getLoader().refresh()

    override suspend fun loadNextHistoryTaskPage(): Effect<Completable> = getLoader().loadNext()

    override suspend fun postToInstagram(): Effect<Completable> {
        return apiCall(ioDispatcher) {
            tasksApi.instagramPost()
        }
    }
}