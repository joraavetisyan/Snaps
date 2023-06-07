package io.snaps.basesources.remotedata

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.snaps.basesources.remotedata.model.BannerDto
import io.snaps.basesources.remotedata.model.SocialPageDto
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.json.KotlinxSerializationJsonProvider
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.decodeFromStream

class RemoteDataProviderFirebaseRemoteConfigImpl : RemoteDataProvider {

    private val firebaseRemoteConfig by lazy { FirebaseRemoteConfig.getInstance() }

    private val serializer by lazy { KotlinxSerializationJsonProvider().get() }
    
    private inline fun <reified T : Any> call(block: () -> T): Effect<T> = try {
        Effect.success(block())
    } catch (e: Exception) {
        log(e)
        Effect.error(AppError.Unknown(cause = e))
    }

    private inline fun <reified T : Any> getFromSting(name: String): Effect<T> {
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

    override suspend fun getBanner(): Effect<BannerDto> {
        return getFromSting("mobile_banner")
    }

    override suspend fun getSocialPages(): Effect<List<SocialPageDto>> {
        return getFromStream("social")
    }
}