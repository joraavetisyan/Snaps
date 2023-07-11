package io.snaps.basenotifications.data

import dagger.Lazy
import io.snaps.basenotifications.domain.NotificationPageModel
import io.snaps.basesubs.data.model.SubsItemResponseDto
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.coroutine.UserSessionCoroutineScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface NotificationsRepository {

    fun getNotificationsState(): StateFlow<NotificationPageModel>

    suspend fun refreshNotifications(): Effect<Completable>

    suspend fun loadNextNotificationPage(): Effect<Completable>
}

class NotificationsRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @UserSessionCoroutineScope private val scope: CoroutineScope,
    private val notificationsApi: Lazy<NotificationsApi>,
    private val loaderFactory: NotificationLoaderFactory,
) : NotificationsRepository {

    // todo source of bugs, better way once back supports
    private var mySubscriptions: List<SubsItemResponseDto>? = null

    private fun getLoader(): NotificationLoader {
        return loaderFactory.get(Unit) {
            PagedLoaderParams(
                action = { from, count ->
                    notificationsApi.get().getNotifications(from = from, count = count)
                },
                pageSize = 20,
                mapper = { it.toModelList() },
            )
        }
    }

    override fun getNotificationsState(): StateFlow<NotificationPageModel> = getLoader().state

    override suspend fun refreshNotifications(): Effect<Completable> = getLoader().refresh()

    override suspend fun loadNextNotificationPage(): Effect<Completable> = getLoader().loadNext()
}