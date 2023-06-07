package io.snaps.basesources

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.basesources.remotedata.RemoteDataProvider
import io.snaps.basesources.remotedata.model.AppUpdateInfoDto
import io.snaps.corecommon.model.BuildInfo
import io.snaps.corecommon.model.Effect
import javax.inject.Inject
import javax.inject.Singleton

interface AppUpdateProvider {

    fun getAvailableUpdateInfo(): Effect<UpdateAvailableState>
}

class AppUpdateProviderImpl @Inject constructor(
    private val buildInfo: BuildInfo,
    private val remoteDataProvider: RemoteDataProvider,
) : AppUpdateProvider {

    override fun getAvailableUpdateInfo(): Effect<UpdateAvailableState> {
        val appUpdateInfo = remoteDataProvider.getAppCurrentVersion().data
        val updateAvailableState = appUpdateInfo?.let {
            if (it.versionCode > buildInfo.versionCode) {
                UpdateAvailableState.Available(it)
            } else {
                UpdateAvailableState.Downloaded
            }
        } ?: UpdateAvailableState.NotAvailable
        return Effect.success(updateAvailableState)
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