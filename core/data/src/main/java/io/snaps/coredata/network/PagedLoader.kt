package io.snaps.coredata.network

import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * [from] - eg, id of the last item, null if nothing loaded yet
 */
typealias PagedLoaderAction<T> = suspend (from: String?, count: Int) -> BaseResponse<List<T>>

// todo in the future, nextPage: Int will be used
data class PagedLoaderParams<T, R>(
    val action: PagedLoaderAction<T>,
    val pageSize: Int,
    val nextPageIdFactory: (T) -> String?,
    val mapper: suspend (List<T>) -> List<R>,
)

data class PageModel<T>(
    val pageSize: Int,
    val loadedPageItems: List<T> = emptyList(),
    val nextPageId: String? = "", // empty string for the first page, null - no next page
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
        val nextPageId = _state.value.nextPageId ?: return Effect.completable

        if (_state.value.isLoading) return Effect.completable

        _state.update { it.copy(isLoading = true, error = null) }
        return apiCall(ioDispatcher) {
            params.action(
                nextPageId.ifEmpty { null },
                params.pageSize,
            )
        }.doOnSuccess { result ->
            _state.update { currentState ->
                currentState.copy(
                    loadedPageItems = currentState.loadedPageItems + params.mapper(result),
                    nextPageId = if (result.size < params.pageSize) {
                        null
                    } else {
                        result.lastOrNull()?.let { params.nextPageIdFactory(it) }
                    },
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