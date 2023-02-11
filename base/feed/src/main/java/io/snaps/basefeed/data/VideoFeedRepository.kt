package io.snaps.basefeed.data

import io.snaps.basefeed.domain.VideoFeedPageModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.network.PagedLoaderParams
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface VideoFeedRepository {

    fun getFeedState(): StateFlow<VideoFeedPageModel>

    suspend fun refreshFeed(): Effect<Completable>

    suspend fun loadNextFeedPage(): Effect<Completable>

    fun getPopularFeedState(query: String): StateFlow<VideoFeedPageModel>

    suspend fun refreshPopularFeed(query: String): Effect<Completable>

    suspend fun loadNextPopularFeedPage(query: String): Effect<Completable>

    fun getUserFeedState(userId: Uuid? = null): StateFlow<VideoFeedPageModel>

    suspend fun refreshUserFeed(userId: Uuid? = null): Effect<Completable>

    suspend fun loadNextUserFeedPage(userId: Uuid? = null): Effect<Completable>
}

class VideoFeedRepositoryImpl @Inject constructor(
    private val videoFeedApi: VideoFeedApi,
    private val loaderFactory: VideoFeedLoaderFactory,
) : VideoFeedRepository {

    private fun getLoader(currencyType: VideoFeedType): VideoFeedLoader {
        return loaderFactory.get(currencyType) {
            when (it) {
                VideoFeedType.Main -> PagedLoaderParams(
                    action = videoFeedApi::feed,
                    pageSize = 3,
                )
                VideoFeedType.Own -> PagedLoaderParams(
                    action = videoFeedApi::feed,
                    pageSize = 15,
                )
                is VideoFeedType.Popular -> PagedLoaderParams(
                    action = videoFeedApi::popularFeed,
                    pageSize = 12,
                )
                is VideoFeedType.User -> PagedLoaderParams(
                    action = videoFeedApi::feed,
                    pageSize = 15,
                )
            }
        }
    }

    override fun getFeedState(): StateFlow<VideoFeedPageModel> =
        getLoader(VideoFeedType.Main).state

    override suspend fun refreshFeed(): Effect<Completable> =
        getLoader(VideoFeedType.Main).refresh()

    override suspend fun loadNextFeedPage(): Effect<Completable> =
        getLoader(VideoFeedType.Main).loadNext()

    override fun getPopularFeedState(query: String): StateFlow<VideoFeedPageModel> =
        getLoader(VideoFeedType.Popular(query)).state

    override suspend fun refreshPopularFeed(query: String): Effect<Completable> =
        getLoader(VideoFeedType.Popular(query)).refresh()

    override suspend fun loadNextPopularFeedPage(query: String): Effect<Completable> =
        getLoader(VideoFeedType.Popular(query)).loadNext()

    override fun getUserFeedState(userId: Uuid?): StateFlow<VideoFeedPageModel> =
        getLoader(type(userId)).state

    private fun type(userId: Uuid?) =
        if (userId != null) VideoFeedType.User(userId) else VideoFeedType.Own

    override suspend fun refreshUserFeed(userId: Uuid?): Effect<Completable> =
        getLoader(type(userId)).refresh()

    override suspend fun loadNextUserFeedPage(userId: Uuid?): Effect<Completable> =
        getLoader(type(userId)).loadNext()
}