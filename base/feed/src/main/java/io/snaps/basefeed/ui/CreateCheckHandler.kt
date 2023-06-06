package io.snaps.basefeed.ui

import io.snaps.basefeed.data.VideoFeedRepository
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

// todo rename
interface CreateCheckHandler {

    val createCheckCommand: Flow<Command>

    suspend fun tryOpenCreate()

    sealed class Command {
        object OpenCreateScreen : Command()
    }
}

class CreateCheckHandlerImplDelegate @Inject constructor(
    private val notificationsSource: NotificationsSource,
    @Bridged private val videoFeedRepository: VideoFeedRepository,
    @Bridged private val profileRepository: ProfileRepository,
) : CreateCheckHandler {

    private val _command = Channel<CreateCheckHandler.Command>()
    override val createCheckCommand = _command.receiveAsFlow()

    override suspend fun tryOpenCreate() {
        val (isAllowed, maxCount) = videoFeedRepository.isAllowedToCreate(profileRepository.state.value.dataOrCache)
        if (isAllowed) {
            _command publish CreateCheckHandler.Command.OpenCreateScreen
        } else {
            notificationsSource.sendError(StringKey.ErrorCreateVideoLimit.textValue(maxCount.toString()))
        }
    }
}