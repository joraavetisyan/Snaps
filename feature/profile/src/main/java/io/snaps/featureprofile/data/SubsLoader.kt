package io.snaps.featureprofile.data

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.snaps.corecommon.model.Uuid
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.Action
import io.snaps.coredata.network.PagedLoader
import io.snaps.coredata.network.PagedLoaderFactory
import io.snaps.coredata.network.PagedLoaderParams
import io.snaps.featureprofile.data.model.SubscriptionItemResponseDto
import io.snaps.featureprofile.domain.SubModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

sealed interface SubType {

    data class Subscription(val userId: Uuid?) : SubType

    data class Subscriber(val userId: Uuid?) : SubType
}

class SubsLoader @AssistedInject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    action: Action,
    @Assisted private val params: PagedLoaderParams<SubscriptionItemResponseDto, SubModel>,
) : PagedLoader<SubscriptionItemResponseDto, SubModel>(
    ioDispatcher = ioDispatcher,
    scope = scope,
    action = action,
    params = params,
)

@AssistedFactory
abstract class SubsLoaderFactory :
    PagedLoaderFactory<SubType, SubsLoader, SubscriptionItemResponseDto, SubModel>() {

    override fun provide(params: PagedLoaderParams<SubscriptionItemResponseDto, SubModel>) = create(params)

    abstract fun create(params: PagedLoaderParams<SubscriptionItemResponseDto, SubModel>): SubsLoader
}