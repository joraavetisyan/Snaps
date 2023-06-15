package io.snaps.basesettings.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basesources.remotedata.model.AppUpdateInfoDto
import io.snaps.corecommon.model.BuildInfo
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coreui.viewmodel.likeStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface AppUpdateProvider {

    val state: StateFlow<UpdateAvailableState>
}

class AppUpdateProviderImpl @Inject constructor(
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val buildInfo: BuildInfo,
    private val settingsRepository: SettingsRepository,
) : AppUpdateProvider {

    override val state = settingsRepository.state.map {
        when (it) {
            is Loading -> UpdateAvailableState.NotAvailable
            is Effect -> when {
                it.isSuccess -> requireNotNull(it.requireData.appUpdateInfo.map())
                else -> UpdateAvailableState.NotAvailable
            }
        }
    }.likeStateFlow(scope, UpdateAvailableState.NotAvailable)

    private fun AppUpdateInfoDto.map(): UpdateAvailableState {
        return if (this.versionCode > buildInfo.versionCode) {
            UpdateAvailableState.Available(this)
        } else {
            UpdateAvailableState.Downloaded
        }
    }
}

sealed class UpdateAvailableState {
    data class Available(val info: AppUpdateInfoDto) : UpdateAvailableState()
    object NotAvailable : UpdateAvailableState()
    object Downloaded : UpdateAvailableState()
}

@Module
@InstallIn(SingletonComponent::class)
interface AppUpdateModule {

    @Binds
    @Singleton
    fun appUpdateProvider(provider: AppUpdateProviderImpl): AppUpdateProvider
}