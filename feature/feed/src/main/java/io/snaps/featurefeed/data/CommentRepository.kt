package io.snaps.featurefeed.data

import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.featurefeed.domain.CommentPageModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface CommentRepository {

    fun getCommentsState(videoId: Uuid): StateFlow<CommentPageModel>

    suspend fun refreshComments(videoId: Uuid): Effect<Completable>

    suspend fun loadNextCommentPage(videoId: Uuid): Effect<Completable>
}

class CommentRepositoryImpl @Inject constructor(
    private val commentApi: CommentApi,
    private val loaderFactory: CommentLoaderFactory,
) : CommentRepository {

    private fun getLoader(videoId: Uuid): CommentLoader {
        return loaderFactory.get(videoId) {
            PagedLoaderParams(
                action = { from, count ->
                    commentApi.comments(from = from, count = count, videoId = videoId)
                },
                pageSize = 20,
            )
        }
    }

    override fun getCommentsState(videoId: Uuid): StateFlow<CommentPageModel> =
        getLoader(videoId).state

    override suspend fun refreshComments(videoId: Uuid): Effect<Completable> =
        getLoader(videoId).refresh()

    override suspend fun loadNextCommentPage(videoId: Uuid): Effect<Completable> =
        getLoader(videoId).loadNext()
}