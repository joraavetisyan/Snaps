package io.snaps.basenotifications.data

import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.network.Action
import io.snaps.coredata.network.BaseResponse
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

typealias PagedLoaderAction<T> = suspend (from: Int, count: Int) -> BaseResponse<List<T>>

// todo use common PadedLoader from coredata.networks
data class PagedLoaderParams<T, R>(
    val action: PagedLoaderAction<T>,
    val pageSize: Int,
    val mapper: suspend (List<T>) -> List<R>,
)

data class PageModel<T>(
    val pageSize: Int,
    val loadedPageItems: List<T> = emptyList(),
    val nextPage: Int? = 0, // null - no next page
    val isLoading: Boolean = false,
    val error: AppError? = null,
)

abstract class PagedLoader<T, R>(
    scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val action: Action,
    private val params: PagedLoaderParams<T, R>,
) {

    private val _state = MutableStateFlow(initialPageModel)
    val state = _state.asStateFlow()

    private val initialPageModel get() = PageModel<R>(pageSize = params.pageSize)

    init {
        log("init")
        scope.launch { action.execute { load() } }
    }

    suspend fun refresh(): Effect<Completable> {
        log("refresh")
        if (_state.value.isLoading) return Effect.completable // todo not synced with init load
        _state.update { initialPageModel }
        return action.execute { load() }
    }

    suspend fun loadNext(): Effect<Completable> {
        log("loadNext")
        if (_state.value.isLoading) return Effect.completable // todo not synced with init load
        return action.execute { load() }
    }

    private suspend fun load(): Effect<Completable> {
        val nextPage = _state.value.nextPage ?: return Effect.completable

        if (_state.value.isLoading) return Effect.completable

        _state.update { it.copy(isLoading = true, error = null) }
        return apiCall(ioDispatcher) {
            params.action(
                nextPage * params.pageSize,
                params.pageSize,
            )
        }.doOnSuccess { result ->
            _state.update { currentState ->
                currentState.copy(
                    loadedPageItems = currentState.loadedPageItems + params.mapper(result),
                    nextPage = if (result.size < params.pageSize) null else nextPage + 1,
                    isLoading = false,
                    pageSize = params.pageSize,
                    error = null,
                )
            }
        }.doOnError { error, _ ->
            _state.update { it.copy(isLoading = false, error = error) }
        }.toCompletable()
    }
}

abstract class PagedLoaderFactory<K, L, T, R> where L : PagedLoader<T, R> {

    private val loadersMap: HashMap<K, L> = hashMapOf()

    fun get(key: K, params: (K) -> PagedLoaderParams<T, R>): L = loadersMap.getOrPut(key) { provide(params(key)) }

    operator fun get(key: K): L? = loadersMap[key]

    abstract fun provide(params: PagedLoaderParams<T, R>): L

    fun clear() = loadersMap.clear()
}