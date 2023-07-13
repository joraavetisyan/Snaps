package io.snaps.basenotifications.data

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.snaps.basenotifications.data.model.NotificationItemResponseDto
import io.snaps.basenotifications.domain.NotificationModel
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.coroutine.UserSessionCoroutineScope
import io.snaps.coredata.network.Action
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

class NotificationLoader @AssistedInject constructor(
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
    @UserSessionCoroutineScope scope: CoroutineScope,
    action: Action,
    @Assisted params: PagedLoaderParams<NotificationItemResponseDto, NotificationModel>,
) : PagedLoader<NotificationItemResponseDto, NotificationModel>(
    ioDispatcher = ioDispatcher,
    scope = scope,
    action = action,
    params = params,
)

@AssistedFactory
abstract class NotificationLoaderFactory :
    PagedLoaderFactory<Unit, NotificationLoader, NotificationItemResponseDto, NotificationModel>() {

    override fun provide(params: PagedLoaderParams<NotificationItemResponseDto, NotificationModel>) = create(params)

    abstract fun create(params: PagedLoaderParams<NotificationItemResponseDto, NotificationModel>): NotificationLoader
}