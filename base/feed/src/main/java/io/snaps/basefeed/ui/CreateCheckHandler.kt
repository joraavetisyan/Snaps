package io.snaps.basefeed.ui

import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.basesettings.data.SettingsRepository
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.di.Bridged
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.time.ZoneOffset
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
    private val settingsRepository: SettingsRepository,
    private val userDataStorage: UserDataStorage,
    @Bridged private val profileRepository: ProfileRepository,
) : CreateCheckHandler {

    private val _command = Channel<CreateCheckHandler.Command>()
    override val createCheckCommand = _command.receiveAsFlow()

    override suspend fun tryOpenCreate() {
        val (isAllowed, maxCount) = isAllowedToCreate(profileRepository.state.value.dataOrCache)
        if (isAllowed) {
            _command publish CreateCheckHandler.Command.OpenCreateScreen
        } else {
            notificationsSource.sendError(StringKey.ErrorCreateVideoLimit.textValue(maxCount.toString()))
        }
    }

    // todo delete once checked on backend
    private fun isAllowedToCreate(userInfoModel: UserInfoModel?): Pair<Boolean, Int> {
        val date = userInfoModel?.questInfo?.questDate?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: return true to 0
        val maxCount = settingsRepository.state.value.dataOrCache?.maxVideosCount ?: return true to 0
        val currentCount = userDataStorage.getCreatedVideoCount(userInfoModel.userId, date)
        return (currentCount < maxCount) to maxCount
    }
}