package io.snaps.featurefeed.data

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
import io.snaps.featurefeed.data.model.CommentResponseDto
import io.snaps.featurefeed.domain.CommentModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

class CommentLoader @AssistedInject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    action: Action,
    @Assisted private val params: PagedLoaderParams<CommentResponseDto>,
) : PagedLoader<CommentResponseDto, CommentModel>(
    ioDispatcher = ioDispatcher,
    scope = scope,
    action = action,
    params = params,
    mapper = List<CommentResponseDto>::toModelList,
)

@Singleton
@AssistedFactory
abstract class CommentLoaderFactory :
    PagedLoaderFactory<Uuid, CommentLoader, CommentResponseDto, CommentModel>() {

    override fun provide(params: PagedLoaderParams<CommentResponseDto>) = create(params)

    abstract fun create(params: PagedLoaderParams<CommentResponseDto>): CommentLoader
}