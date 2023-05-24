package io.snaps.basesources

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.BuildInfo
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.FullUrl
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.json.KotlinxSerializationJsonProvider
import javax.inject.Inject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import javax.inject.Singleton

interface AppUpdateProvider {

    fun getAvailableUpdateInfo(): Effect<UpdateAvailableState>
}

class AppUpdateProviderImpl @Inject constructor(
    private val buildInfo: BuildInfo,
    private val userDataStorage: UserDataStorage,
) : AppUpdateProvider {

    override fun getAvailableUpdateInfo(): Effect<UpdateAvailableState> {
        val appUpdateInfo = try {
            FirebaseRemoteConfig.getInstance().getValue("android_version").let {
                KotlinxSerializationJsonProvider().get()
                    .decodeFromString<AppUpdateInfo>(it.asString())
            }
        } catch (e: Exception) {
            log(e)
            null
        }
        val updateAvailableState = appUpdateInfo?.let {
            userDataStorage.lastCheckedAvailableVersionCode = it.versionCode
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
    data class Available(val info: AppUpdateInfo) : UpdateAvailableState()
    object NotAvailable : UpdateAvailableState()
    object Downloaded : UpdateAvailableState()
}

@Serializable
data class AppUpdateInfo(
    @SerialName("version") val versionCode: Int,
    @SerialName("link") val link: FullUrl,
)

@Module
@InstallIn(SingletonComponent::class)
interface AppUpdateModule {

    @Binds
    @Singleton
    fun appUpdateProvider(provider: AppUpdateProviderImpl): AppUpdateProvider
}