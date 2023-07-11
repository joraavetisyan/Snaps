package io.snaps.basesubs.data

import dagger.Lazy
import io.snaps.basesubs.data.model.SubsItemResponseDto
import io.snaps.basesubs.data.model.SubscribeRequestDto
import io.snaps.basesubs.data.model.UnsubscribeRequestDto
import io.snaps.basesubs.domain.SubPageModel
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface SubsRepository {

    fun getSubscriptionsState(userId: Uuid?): StateFlow<SubPageModel>

    suspend fun refreshSubscriptions(userId: Uuid?): Effect<Completable>

    suspend fun loadNextSubscriptionsPage(userId: Uuid?): Effect<Completable>

    fun getSubscribersState(userId: Uuid?): StateFlow<SubPageModel>

    suspend fun refreshSubscribers(userId: Uuid?): Effect<Completable>

    suspend fun loadNextSubscribersPage(userId: Uuid?): Effect<Completable>

    suspend fun subscribe(toSubscribeUserId: Uuid): Effect<Completable>

    suspend fun unsubscribe(subscriptionId: Uuid): Effect<Completable>

    suspend fun isSubscribed(userId: Uuid): Effect<Boolean>

    suspend fun mySubscriptions(): Effect<List<SubsItemResponseDto>>
}

class SubsRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val subsApi: Lazy<SubsApi>,
    private val loaderFactory: SubsLoaderFactory,
) : SubsRepository {

    // todo source of bugs, better way once back supports
    private var mySubscriptions: List<SubsItemResponseDto>? = null

    private fun getLoader(subType: SubType): SubsLoader {
        return loaderFactory.get(subType) { type ->
            when (type) {
                is SubType.Subscription -> PagedLoaderParams(
                    action = { from, count ->
                        if (type.userId == null) {
                            subsApi.get().mySubscriptions(from = from, count = count)
                        } else {
                            subsApi.get().subscriptions(from = from, count = count, userId = type.userId)
                        }
                    },
                    pageSize = 100,
                    nextPageIdFactory = { it.entityId },
                    mapper = { it.toModelList(mySubscriptions = mySubscriptions().data) },
                )
                is SubType.Subscriber -> PagedLoaderParams(
                    action = { from, count ->
                        if (type.userId == null) {
                            subsApi.get().mySubscribers(from = from, count = count)
                        } else {
                            subsApi.get().subscribers(from = from, count = count, userId = type.userId)
                        }
                    },
                    pageSize = 100,
                    nextPageIdFactory = { it.entityId },
                    mapper = { it.toModelList(mySubscriptions = mySubscriptions().data) },
                )
            }
        }
    }

    override fun getSubscriptionsState(userId: Uuid?): StateFlow<SubPageModel> =
        getLoader(SubType.Subscription(userId)).state

    override suspend fun refreshSubscriptions(userId: Uuid?): Effect<Completable> =
        getLoader(SubType.Subscription(userId)).refresh()

    override suspend fun loadNextSubscriptionsPage(userId: Uuid?): Effect<Completable> =
        getLoader(SubType.Subscription(userId)).loadNext()

    override fun getSubscribersState(userId: Uuid?): StateFlow<SubPageModel> =
        getLoader(SubType.Subscriber(userId)).state

    override suspend fun refreshSubscribers(userId: Uuid?): Effect<Completable> =
        getLoader(SubType.Subscriber(userId)).refresh()

    override suspend fun loadNextSubscribersPage(userId: Uuid?): Effect<Completable> =
        getLoader(SubType.Subscriber(userId)).loadNext()

    override suspend fun subscribe(toSubscribeUserId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) {
            subsApi.get().subscribe(
                SubscribeRequestDto(toSubscribeUserId = toSubscribeUserId)
            )
        }.doOnSuccess {
            mySubscriptions = mySubscriptions?.plus(
                SubsItemResponseDto(
                    userId = toSubscribeUserId,
                    entityId = "dummy",
                    avatar = null,
                    name = null,
                )
            )
            loaderFactory[SubType.Subscriber(toSubscribeUserId)]?.refresh()
            refreshSubscribers(null)
            refreshSubscriptions(null)
        }
    }

    override suspend fun unsubscribe(subscriptionId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) {
            subsApi.get().unsubscribe(
                UnsubscribeRequestDto(subscriptionId = subscriptionId)
            )
        }.doOnSuccess {
            mySubscriptions = mySubscriptions?.filter { it.userId != subscriptionId }
            loaderFactory[SubType.Subscriber(subscriptionId)]?.refresh()
            refreshSubscribers(null)
            refreshSubscriptions(null)
        }
    }

    override suspend fun isSubscribed(userId: Uuid): Effect<Boolean> {
        return mySubscriptions().map { subscriptions -> subscriptions.any { it.userId == userId } }
    }

    override suspend fun mySubscriptions(): Effect<List<SubsItemResponseDto>> {
        return mySubscriptions?.let(Effect.Companion::success) ?: apiCall(ioDispatcher) {
            subsApi.get().mySubscriptions(from = null, count = 1000)
        }.doOnSuccess {
            mySubscriptions = it
        }
    }
}