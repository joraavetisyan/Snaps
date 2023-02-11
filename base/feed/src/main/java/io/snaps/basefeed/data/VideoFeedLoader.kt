package io.snaps.basefeed.data

import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.Action
import io.snaps.coredata.network.BaseResponse
import io.snaps.coredata.network.apiCall
import io.snaps.basefeed.data.model.VideoFeedItemResponseDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface VideoFeedType {

    object Main : VideoFeedType

    data class Popular(val query: String) : VideoFeedType

    data class User(val userId: Uuid) : VideoFeedType

    object Own : VideoFeedType
}

typealias VideoFeedLoaderAction = suspend (
    from: Int,
    count: Int,
) -> BaseResponse<List<VideoFeedItemResponseDto>>

data class VideoFeedLoaderParams(
    val action: VideoFeedLoaderAction,
    val pageSize: Int,
)

class VideoFeedLoader @AssistedInject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val action: Action,
    @Assisted private val params: VideoFeedLoaderParams,
) {

    private val _state = MutableStateFlow(initialPageModel)
    val state = _state.asStateFlow()

    private val initialPageModel get() = io.snaps.basefeed.domain.VideoFeedPageModel(pageSize = params.pageSize)

    init {
        scope.launch { action.execute { load() } }
    }

    suspend fun refresh(): Effect<Completable> {
        _state.update { initialPageModel }
        return load()
    }

    suspend fun loadNext(): Effect<Completable> = load()

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
                    loadedPageItems = currentState.loadedPageItems + result.toModelList(),
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