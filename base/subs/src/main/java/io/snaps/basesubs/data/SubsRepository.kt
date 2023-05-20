package io.snaps.basesubs.data

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
}

class SubsRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val subsApi: SubsApi,
    private val loaderFactory: SubsLoaderFactory,
) : SubsRepository {

    private var mySubscriptions: List<SubsItemResponseDto>? = null

    private fun getLoader(subType: SubType): SubsLoader {
        return loaderFactory.get(subType) { type ->
            when (type) {
                is SubType.Subscription -> PagedLoaderParams(
                    action = { from, count ->
                        if (type.userId == null) {
                            subsApi.mySubscriptions(from = from, count = count)
                        } else {
                            subsApi.subscriptions(from = from, count = count, userId = type.userId)
                        }
                    },
                    pageSize = 100,
                    nextPageIdFactory = { it.userId },
                    mapper = { it.toModelList(mySubscriptions = mySubscriptions().data) },
                )
                is SubType.Subscriber -> PagedLoaderParams(
                    action = { from, count ->
                        if (type.userId == null) {
                            subsApi.mySubscribers(from = from, count = count)
                        } else {
                            subsApi.subscribers(from = from, count = count, userId = type.userId)
                        }
                    },
                    pageSize = 100,
                    nextPageIdFactory = { it.userId },
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
            subsApi.subscribe(
                SubscribeRequestDto(toSubscribeUserId = toSubscribeUserId)
            )
        }
    }

    override suspend fun unsubscribe(subscriptionId: Uuid): Effect<Completable> {
        return apiCall(ioDispatcher) {
            subsApi.unsubscribe(
                UnsubscribeRequestDto(subscriptionId = subscriptionId)
            )
        }
    }

    override suspend fun isSubscribed(userId: Uuid): Effect<Boolean> {
        return mySubscriptions().map { subscriptions -> subscriptions.any { it.userId == userId } }
    }

    private suspend fun mySubscriptions(): Effect<List<SubsItemResponseDto>> {
        return mySubscriptions?.let(Effect.Companion::success) ?: apiCall(ioDispatcher) {
            subsApi.mySubscriptions(from = null, count = 1000)
        }.doOnSuccess {
            mySubscriptions = it
        }
    }
}