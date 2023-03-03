package io.snaps.featuretasks.data

import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.apiCall
import io.snaps.featuretasks.domain.TaskModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

interface TasksRepository {

    suspend fun historyTasks(): Effect<List<TaskModel>>

    suspend fun taskById(id: Uuid): Effect<TaskModel>
}

class TasksRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val tasksApi: TasksApi,
) : TasksRepository {

    override suspend fun historyTasks(): Effect<List<TaskModel>> {
        return apiCall(ioDispatcher) {
            tasksApi.historyTasks()
        }.map {
            it.toModelList()
        }
    }

    override suspend fun taskById(id: Uuid): Effect<TaskModel> {
        return apiCall(ioDispatcher) {
            tasksApi.task(id)
        }.map {
            it.toTaskModel()
        }
    }
}