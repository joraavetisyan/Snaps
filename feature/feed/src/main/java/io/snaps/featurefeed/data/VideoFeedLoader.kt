package io.snaps.featurefeed.data

import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.Action
import io.snaps.coredata.network.apiCall
import io.snaps.featurefeed.domain.VideoFeedPageModel
import io.snaps.featurefeed.domain.VideoFeedPageSize
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class VideoFeedLoader @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val action: Action,
    private val videoFeedApi: VideoFeedApi,
) {

    private val _state = MutableStateFlow(VideoFeedPageModel())
    val state = _state.asStateFlow()

    init {
        scope.launch { action.execute { load() } }
    }

    suspend fun update(): Effect<Completable> {
        _state.update { VideoFeedPageModel() }
        return load()
    }

    suspend fun loadNextPage(): Effect<Completable> = load()

    private suspend fun load(): Effect<Completable> {
        val nextPage = _state.value.nextPage ?: return Effect.success(Completable)

        _state.update { it.copy(isLoading = true, error = null) }
        return apiCall(ioDispatcher) {
            videoFeedApi.feed(
                from = nextPage * VideoFeedPageSize,
                count = VideoFeedPageSize,
            )
        }.doOnSuccess { result ->
            _state.update { currentState ->
                currentState.copy(
                    loadedPageItems = currentState.loadedPageItems + result.toModelList(),
                    nextPage = if (result.size < VideoFeedPageSize) null else nextPage + 1,
                    isLoading = false,
                    error = null,
                )
            }
        }.doOnError { error, _ ->
            _state.update { it.copy(isLoading = false, error = error) }
        }.toCompletable()
    }
}