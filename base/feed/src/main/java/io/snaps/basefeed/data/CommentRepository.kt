package io.snaps.basefeed.data

import dagger.Lazy
import io.snaps.basefeed.data.model.CreateCommentRequestDto
import io.snaps.basefeed.domain.CommentPageModel
import io.snaps.baseprofile.data.ProfileApi
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.BaseResponse
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface CommentRepository {

    fun getCommentsState(videoId: Uuid): StateFlow<CommentPageModel>

    suspend fun refreshComments(videoId: Uuid): Effect<Completable>

    suspend fun loadNextCommentPage(videoId: Uuid): Effect<Completable>

    suspend fun createComment(videoId: Uuid, text: String): Effect<Completable>
}

class CommentRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val commentApi: Lazy<CommentApi>,
    private val profileApi: Lazy<ProfileApi>,
    private val loaderFactory: CommentLoaderFactory,
) : CommentRepository {

    private var users: HashMap<Uuid, UserInfoResponseDto?> = hashMapOf()

    private fun getLoader(videoId: Uuid): CommentLoader {
        return loaderFactory.get(videoId) {
            PagedLoaderParams(
                action = { from, count ->
                    val comments = commentApi.get()
                        .comments(videoId = videoId, from = from, count = count).data?.filter {
                        it.text.isNotEmpty()
                    }
                    BaseResponse(comments)
                },
                pageSize = 20,
                nextPageIdFactory = { it.id },
                mapper = { it.toCommentModelList(::getUser) },
            )
        }
    }

    private suspend fun getUser(id: Uuid): UserInfoResponseDto? {
        return users.getOrPut(id) {
            apiCall(ioDispatcher) { profileApi.get().userInfo(id) }.dataOrCache
        }
    }

    override fun getCommentsState(videoId: Uuid): StateFlow<CommentPageModel> =
        getLoader(videoId).state

    override suspend fun refreshComments(videoId: Uuid): Effect<Completable> =
        getLoader(videoId).refresh()

    override suspend fun loadNextCommentPage(videoId: Uuid): Effect<Completable> =
        getLoader(videoId).loadNext()

    override suspend fun createComment(videoId: Uuid, text: String): Effect<Completable> {
        return apiCall(ioDispatcher) {
            commentApi.get().createComment(
                videoId = videoId,
                body = CreateCommentRequestDto(text),
            )
        }
    }
}