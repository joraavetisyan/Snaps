package io.snaps.coredata.network

import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

typealias PagedLoaderAction<T> = suspend (from: Int, count: Int) -> BaseResponse<List<T>>

data class PagedLoaderParams<T>(
    val action: PagedLoaderAction<T>,
    val pageSize: Int,
)

data class PageModel<T>(
    val pageSize: Int,
    val loadedPageItems: List<T> = emptyList(),
    val nextPage: Int? = 0,
    val isLoading: Boolean = false,
    val error: AppError? = null,
)

abstract class PagedLoader<T, R>(
    private val scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val action: Action,
    private val params: PagedLoaderParams<T>,
    private val mapper: (List<T>) -> List<R>,
) {

    private val _state = MutableStateFlow(initialPageModel)
    val state = _state.asStateFlow()

    private val initialPageModel get() = PageModel<R>(pageSize = params.pageSize)

    init {
        scope.launch { action.execute { load() } }
    }

    suspend fun refresh(): Effect<Completable> {
        _state.update { initialPageModel }
        return action.execute { load() }
    }

    suspend fun loadNext(): Effect<Completable> = action.execute { load() }

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
                    loadedPageItems = currentState.loadedPageItems + mapper(result),
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

    fun get(key: K, params: (K) -> PagedLoaderParams<T>): L =
        loadersMap.getOrPut(key) { provide(params(key)) }

    abstract fun provide(params: PagedLoaderParams<T>): L
}