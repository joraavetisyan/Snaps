package io.snaps.baseprofile.data

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.snaps.baseprofile.data.model.UserInfoResponseDto
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.Action
import io.snaps.coredata.network.PagedLoader
import io.snaps.coredata.network.PagedLoaderFactory
import io.snaps.coredata.network.PagedLoaderParams
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

class UsersLoader @AssistedInject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    action: Action,
    @Assisted private val params: PagedLoaderParams<UserInfoResponseDto, UserInfoModel>,
) : PagedLoader<UserInfoResponseDto, UserInfoModel>(
    ioDispatcher = ioDispatcher,
    scope = scope,
    action = action,
    params = params,
)

@AssistedFactory
abstract class UsersLoaderFactory :
    PagedLoaderFactory<String, UsersLoader, UserInfoResponseDto, UserInfoModel>() {

    override fun provide(params: PagedLoaderParams<UserInfoResponseDto, UserInfoModel>) = create(params)

    abstract fun create(params: PagedLoaderParams<UserInfoResponseDto, UserInfoModel>): UsersLoader
}