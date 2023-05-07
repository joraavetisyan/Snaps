package io.snaps.basesubs.data

import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.coredata.network.apiCall
import io.snaps.basesubs.data.model.SubscribeRequestDto
import io.snaps.basesubs.data.model.SubscriptionItemResponseDto
import io.snaps.basesubs.data.model.UnsubscribeRequestDto
import io.snaps.basesubs.domain.SubPageModel
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

    suspend fun getSubscriptions(): List<UserInfoResponseDto>
}

class SubsRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val subsApi: SubsApi,
    private val loaderFactory: SubsLoaderFactory,
) : SubsRepository {

    private var subscriptions: List<UserInfoResponseDto>? = null

    private fun getLoader(subType: SubType): SubsLoader {
        return loaderFactory.get(subType) { type ->
            when (type) {
                is SubType.Subscription -> PagedLoaderParams(
                    action = { from, count ->
                        subsApi.subscriptions(from = from, count = count, userId = type.userId)
                    },
                    pageSize = 20,
                    nextPageIdFactory = { it.userId },
                    mapper = List<SubscriptionItemResponseDto>::toModelList,
                )
                is SubType.Subscriber -> PagedLoaderParams(
                    action = { from, count ->
                        subsApi.subscribers(from = from, count = count, userId = type.userId)
                    },
                    pageSize = 20,
                    nextPageIdFactory = { it.userId },
                    mapper = List<SubscriptionItemResponseDto>::toModelList,
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

     override suspend fun getSubscriptions(): List<UserInfoResponseDto> {
        return subscriptions ?: apiCall(ioDispatcher) {
            subsApi.subscriptions(null, 100)
        }.doOnSuccess {
            subscriptions = it
        }.dataOrCache ?: emptyList()
    }
}