package io.snaps.basesources.remotedata

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.snaps.basesources.remotedata.model.AppUpdateInfoDto
import io.snaps.basesources.remotedata.model.BannerDto
import io.snaps.basesources.remotedata.model.SocialPageDto
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.BuildInfo
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.json.KotlinxSerializationJsonProvider
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.decodeFromStream

// todo now SettingsApi is used
class RemoteDataProviderFirebaseRemoteConfigImpl(
    private val buildInfo: BuildInfo,
) : RemoteDataProvider {

    // Fetch and activate done in FeatureToggleUpdater, todo can lead to inconsistency
    private val firebaseRemoteConfig by lazy { FirebaseRemoteConfig.getInstance() }

    private val serializer by lazy { KotlinxSerializationJsonProvider().get() }

    private inline fun <reified T : Any> call(block: () -> T): Effect<T> = try {
        Effect.success(block().also { log("Fetched remote data: $it") })
    } catch (e: Exception) {
        log(e, "Remote data fetch error!")
        Effect.error(AppError.Unknown(cause = e))
    }

    private inline fun <reified T : Any> getFromString(name: String): Effect<T> {
        return call {
            serializer.decodeFromString(firebaseRemoteConfig.getValue(name).asString())
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private inline fun <reified T : Any> getFromStream(name: String): Effect<T> {
        return call {
            serializer.decodeFromStream(firebaseRemoteConfig.getValue(name).asByteArray().inputStream())
        }
    }

    override fun getBanner(): Effect<BannerDto> {
        return getFromString("mobile_banner")
    }

    override fun getSocialPages(): Effect<List<SocialPageDto>> {
        return getFromStream("social")
    }

    override fun getMaxVideoCount(): Effect<Int> {
        return call {
            firebaseRemoteConfig.getLong("max_videos_count").toInt()
        }
    }

    override fun getAppCurrentVersion(): Effect<AppUpdateInfoDto> {
        return getFromString("android_version")
    }

    override fun getVideoapiKey(): Effect<String> {
        return call {
            firebaseRemoteConfig.getString(if (buildInfo.isRelease) "video_key" else "video_key_dev")
        }
    }
}